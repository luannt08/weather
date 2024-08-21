package com.lu.weather.data.repository.weatherbit

import android.util.Log
import androidx.annotation.WorkerThread
import com.lu.weather.common.extension.dataToCurrentWeatherRealm
import com.lu.weather.common.extension.dataToDailyForecastRealm
import com.lu.weather.common.extension.dataToHourlyForecastRealm
import com.lu.weather.common.extension.realmToCityData
import com.lu.weather.common.extension.responseToCurrentForecastRealm
import com.lu.weather.common.extension.responseToDailyForecastRealm
import com.lu.weather.common.extension.responseToHourlyForecastRealm
import com.lu.weather.common.extension.toCurrentForecastData
import com.lu.weather.common.extension.toDailyForecastData
import com.lu.weather.common.extension.toHourlyForecastData
import com.lu.weather.data.model.CityData
import com.lu.weather.data.model.LocationData
import com.lu.weather.data.model.networking.CurrentWeatherResponse
import com.lu.weather.data.model.networking.DailyForecastResponse
import com.lu.weather.data.model.networking.HourlyForecastResponse
import com.lu.weather.data.model.networking.NetworkingError
import com.lu.weather.data.model.realm.DailyForecastRealm
import com.lu.weather.data.model.realm.HourlyForecastRealm
import com.lu.weather.data.source.IRealmDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherAPIService: WeatherAPIService, private val realmDataSource: IRealmDataSource
) : IWeatherRepository {

    companion object {
        const val NUMBER_OF_DAILY = 7
        const val NUMBER_OF_HOURLY = 24

        const val INTERNET_CONNECTION_ERROR = 1
        const val UNEXPECTED_ERROR = 2
    }

    private val citiesFlow = realmDataSource.getAllCitiesFlow().map { citiesRealm ->
        citiesRealm.map {
            it.realmToCityData()
        }
    }

    private val focusedLocationFlow = MutableStateFlow<String?>(null) //lat-lon
    private val focusedCityFlow: Flow<Pair<CityData, Boolean>> by lazy {
        channelFlow {
            focusedLocationFlow.collectLatest { location ->
                if (location == null) {
                    return@collectLatest
                }

                if (realmDataSource.checkAvailableOfCity(location)) {
                    realmDataSource.getCityFlow(location).collectLatest { changes ->
                        changes.obj?.let { city ->
                            send(city.realmToCityData() to true)
                        }
                    }
                } else {
                    val cityData = fetchCityWeatherBySearch(location)
                    send(cityData to false)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    private val errorEvent = MutableSharedFlow<NetworkingError>()

    override suspend fun fetchAllCitiesForecast() = withContext(Dispatchers.IO) {
        val listOfErrors = CopyOnWriteArrayList<Throwable>()

        realmDataSource.getAllCitiesLocation().map { city ->
            async {
                try {
                    fetchCityWeatherApplyToRealm(city, listOfErrors)
                } catch (ex: Exception) {
                    listOfErrors.add(ex)
                    ex.printStackTrace()
                }
            }
        }.awaitAll()

        handleError(listOfErrors)
    }

    @WorkerThread
    private suspend fun handleError(listOfErrors: CopyOnWriteArrayList<Throwable>) {
        if (listOfErrors.isEmpty()) {
            return
        }

        val internetException =
            listOfErrors.firstOrNull { it is UnknownHostException || it is TimeoutException
                    || it is ConnectException || it is SocketException }

        delay(2000) // for UI have time to subscribe
        if (internetException == null) {
            errorEvent.emit(NetworkingError(System.currentTimeMillis(), UNEXPECTED_ERROR))
        } else {
            errorEvent.emit(NetworkingError(System.currentTimeMillis(), INTERNET_CONNECTION_ERROR))
        }
        listOfErrors.clear()
    }

    @WorkerThread
    private suspend fun fetchCityWeatherBySearch(cityName: String) = coroutineScope {
        val currentResult = async { weatherAPIService.fetchCurrentWeather(cityName) }.await()
        val hourlyResult =
            async { weatherAPIService.fetchHourlyForecast(cityName, NUMBER_OF_HOURLY) }.await()
        val dailyResult = async { weatherAPIService.fetchDailyForecast(cityName) }.await()

        val listOfErrors = CopyOnWriteArrayList<Throwable>()
        val currentWeatherResponse = currentResult.getOrNull()?.let {
            it.data.firstOrNull()
        } ?: run {
            currentResult.exceptionOrNull()?.let { listOfErrors.add(it) }
            null
        }

        val hourlyForecastData = hourlyResult.getOrNull()?.let { hourlyResponse ->
            hourlyResponse.hours.map {
                it.toHourlyForecastData(cityName, hourlyResponse.timeZone)
            }
        } ?: run {
            hourlyResult.exceptionOrNull()?.let { listOfErrors.add(it) }
            listOf()
        }
        val dailyForecastData = dailyResult.getOrNull()?.let { dailyResponse ->
            dailyResponse.days.take(NUMBER_OF_DAILY)
                .map { it.toDailyForecastData(cityName, dailyResponse.timeZone) }
        } ?: run {
            dailyResult.exceptionOrNull()?.let { listOfErrors.add(it) }
            listOf()
        }

        handleError(listOfErrors)

        return@coroutineScope CityData(
            cityName,
            currentWeatherResponse?.timeZone ?: "",
            currentWeatherResponse?.state ?: "",
            currentWeatherResponse?.countryCode ?: "",
            currentWeatherResponse?.toCurrentForecastData(),
            hourlyForecastData,
            dailyForecastData
        )
    }

    @WorkerThread
    private suspend fun fetchCityWeatherApplyToRealm(
        cityName: String, listOfErrors: CopyOnWriteArrayList<Throwable>
    ) = coroutineScope {
        val currentResult = async { weatherAPIService.fetchCurrentWeather(cityName) }.await()
        val hourlyResult =
            async { weatherAPIService.fetchHourlyForecast(cityName, NUMBER_OF_HOURLY) }.await()
        val dailyResult = async { weatherAPIService.fetchDailyForecast(cityName) }.await()

        handleDailyForecast(dailyResult, listOfErrors)
        handleCurrentHourlyForecast(hourlyResult, listOfErrors)
        handleCurrentWeather(currentResult, dailyResult.getOrNull()?.let { daily ->
            daily.days.take(NUMBER_OF_DAILY)
                .map { it.responseToDailyForecastRealm(cityName, daily.timeZone) }
        } ?: listOf(), hourlyResult.getOrNull()?.let { hourly ->
            hourly.hours.map { it.responseToHourlyForecastRealm(cityName, hourly.timeZone) }
        } ?: listOf(), listOfErrors)
    }

    @WorkerThread
    private suspend fun handleDailyForecast(
        dailyResult: Result<DailyForecastResponse>, listOfErrors: MutableList<Throwable>
    ) {
        val dailyResponse = dailyResult.getOrNull()
        if (dailyResult.isSuccess && dailyResponse != null) {
            realmDataSource.processDailyForecastData(dailyResponse.days.take(NUMBER_OF_DAILY).map {
                it.responseToDailyForecastRealm(dailyResponse.cityName, dailyResponse.timeZone)
            }, dailyResponse.cityName)
        } else {
            dailyResult.exceptionOrNull()?.let {
                listOfErrors.add(it)
                it.printStackTrace()
            }
        }
    }

    @WorkerThread
    private suspend fun handleCurrentHourlyForecast(
        hourlyResult: Result<HourlyForecastResponse>, errors: MutableList<Throwable>
    ) {
        val hourlyResponse = hourlyResult.getOrNull()
        if (hourlyResult.isSuccess && hourlyResponse != null) {
            realmDataSource.processHourlyForecastData(hourlyResponse.hours.map {
                it.responseToHourlyForecastRealm(
                    hourlyResponse.cityName, hourlyResponse.timeZone
                )
            }, hourlyResponse.cityName)
        } else {
            hourlyResult.exceptionOrNull()?.let {
                errors.add(it)
                it.printStackTrace()
            }
        }
    }

    @WorkerThread
    private suspend fun handleCurrentWeather(
        currentResult: Result<CurrentWeatherResponse>,
        dailyForecasts: List<DailyForecastRealm>,
        hourlyForecasts: List<HourlyForecastRealm>,
        errors: MutableList<Throwable>
    ) {
        val currentWeatherData = currentResult.getOrNull()
        if (currentResult.isSuccess && currentWeatherData != null) {
            currentWeatherData.data.firstOrNull()?.let {
                realmDataSource.processCurrentWeather(
                    it.responseToCurrentForecastRealm(),
                    dailyForecasts,
                    hourlyForecasts,
                    it.cityName,
                    it.timeZone,
                    it.state ?: "",
                    it.countryCode ?: ""
                )
            } ?: run {
                Log.e("WeatherRepository", "current weather data is empty!")
            }
        } else {
            currentResult.exceptionOrNull()?.let {
                errors.add(it)
                it.printStackTrace()
            }
        }
    }

    override fun getFocusingCityFlow(cityName: String): Flow<Pair<CityData, Boolean>> {
        focusedLocationFlow.value = cityName
        return focusedCityFlow
    }

    override fun getCitiesFlow() = citiesFlow

    override fun getErrorFlow() = errorEvent

    override suspend fun addFocusingCityForecastToRealm() = withContext(Dispatchers.IO) {
        val cityData = focusedCityFlow.first().first
        val currentWeatherData = cityData.currentWeather
        val dailyForecastsRealm = cityData.dailyForecasts.map { it.dataToDailyForecastRealm() }
        val hourlyForecastsRealm =
            cityData.currentHourlyForecast.map { it.dataToHourlyForecastRealm() }

        if (currentWeatherData != null) {
            realmDataSource.processCurrentWeather(
                currentWeatherData.dataToCurrentWeatherRealm(),
                dailyForecastsRealm,
                hourlyForecastsRealm,
                cityData.cityName,
                cityData.timeZone,
                cityData.state,
                cityData.countryCode
            )
        }
    }

    override suspend fun deleteCityForecast(cityName: String) = withContext(Dispatchers.IO) {
        realmDataSource.deleteCityForecast(cityName)
    }

    override suspend fun searchCity(query: String) = withContext(Dispatchers.IO) {
        val result = weatherAPIService.search(query)
        if (result.isSuccess && !result.getOrNull()?.data.isNullOrEmpty()) {
            return@withContext result.getOrNull()!!.data.map {
                LocationData(
                    it.cityName, it.lat, it.lon, it.countryCode ?: ""
                )
            }
        } else {

            result.exceptionOrNull()?.let {
                val listError = CopyOnWriteArrayList<Throwable>()
                listError.add(it)
                handleError(listError)
                it.printStackTrace()
            }
        }

        return@withContext listOf()
    }
}