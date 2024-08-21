package com.lu.weather.data.source

import android.util.Log
import com.lu.weather.data.model.realm.CityRealm
import com.lu.weather.data.model.realm.CurrentWeatherRealm
import com.lu.weather.data.model.realm.DailyForecastRealm
import com.lu.weather.data.model.realm.HourlyForecastRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RealmDataSource @Inject constructor(private val realm: Realm) : IRealmDataSource {

    override fun checkAvailableOfCity(cityName: String) =
        realm.query<CityRealm>("cityName == $0", cityName).count().find() > 0

    override fun getAllCitiesFlow() = realm.query<CityRealm>().asFlow().map { changes ->
        changes.list.map { city ->
            city
        }
    }

    override fun getAllCurrentWeathersFlow() =
        realm.query<CurrentWeatherRealm>().asFlow().map { changes ->
            changes.list.map { currentWeatherRealm ->
                currentWeatherRealm
            }
        }

    override suspend fun getCityFlow(cityName: String) =
        realm.query<CityRealm>("cityName == $0", cityName).first().asFlow()


    override fun getAllCitiesLocation() = realm.query<CityRealm>().find().map { it.cityName }

    override suspend fun processDailyForecastData(newDailyData: List<DailyForecastRealm>, cityName: String) {
        //Log.e("Realm", "processDailyForecastData - $cityName - ${newDailyData.size} - ${newDailyData.map { it }}")
        realm.write {
            val existingDailyForecastRealms = query<DailyForecastRealm>("id BEGINSWITH[c] $0", cityName).find()
            val newIds = newDailyData.map { it.id }
            val deletedIds = existingDailyForecastRealms.filter { it.id !in newIds }
            deletedIds.forEach {
                delete(it)
                //Log.e("Realm", "processDailyForecastData - $cityName - delete - id: $it")
            }

            newDailyData.forEach { newDailyRealm ->
                val existingDailyForecast =
                    query<DailyForecastRealm>("id == $0", newDailyRealm.id).first().find()
                if (existingDailyForecast == null) {
                    copyToRealm(newDailyRealm)
                    //Log.e("Realm", "processDailyForecastData copyToRealm - $cityName- $newDailyRealm")
                } else {
                    existingDailyForecast.apply {
                        highTemperature = newDailyRealm.highTemperature
                        lowTemperature = newDailyRealm.lowTemperature
                        chanceOfRain = newDailyRealm.chanceOfRain
                        icon = newDailyRealm.icon
                        weatherCondition = newDailyRealm.weatherCondition
                        code = newDailyRealm.code
                    }

                    //Log.e("Realm", "processDailyForecastData update- $cityName - ${existingDailyForecast.id}")
                }
            }
        }
    }

    override suspend fun processHourlyForecastData(newHourlyData: List<HourlyForecastRealm>, cityName: String) {
        //Log.e("Realm", "processHourlyForecastData - $cityName - ${newHourlyData.size} - ${newHourlyData.map { it }}")

        realm.write {
            val existingHourlyForecastRealms = query<HourlyForecastRealm>("id BEGINSWITH[c] $0", cityName).find()
            val newIds = newHourlyData.map { it.id }
            val deletedIds = existingHourlyForecastRealms.filter { it.id !in newIds }
            deletedIds.forEach {
                delete(it)
                //Log.e("Realm", "processHourlyForecastData - $cityName - delete - id: $it")
            }

            newHourlyData.forEach {
                val existingHourlyForecast =
                    query<HourlyForecastRealm>("id == $0", it.id).first().find()
                if (existingHourlyForecast == null) {
                    copyToRealm(it)
                    //Log.e("Realm", "processHourlyForecastData - $cityName - copy- $it")
                } else {
                    existingHourlyForecast.apply {
                        chanceOfRain = it.chanceOfRain
                        temperature = it.temperature
                        icon = it.icon
                        weatherCondition = it.weatherCondition
                        code = it.code
                    }

                    //Log.e("Realm", "processHourlyForecastData - $cityName - update- ${existingHourlyForecast.id}")
                }
            }
        }
    }

    override suspend fun processCurrentWeather(
        newCurrentWeather: CurrentWeatherRealm,
        newDailyData: List<DailyForecastRealm>,
        newHourlyData: List<HourlyForecastRealm>,
        city: String,
        timeZone: String,
        state: String,
        countryCode: String
    ) {
        realm.write {
            var existingCityRealm =
                query<CityRealm>("cityName == $0", newCurrentWeather.cityName).first().find()

            val hourlyForecastsRealm = newHourlyData.toRealmList()
            val dailyForecastsRealm = newDailyData.toRealmList()

            if (existingCityRealm == null) {
                existingCityRealm = CityRealm().apply {
                    cityName = newCurrentWeather.cityName
                    this.timeZone = timeZone
                    this.state = state
                    this.countryCode = countryCode
                    currentWeather = newCurrentWeather
                    currentHourlyForecastRealm = hourlyForecastsRealm
                    this.dailyForecastRealms = dailyForecastsRealm
                }
                copyToRealm(existingCityRealm)
            } else {
                existingCityRealm.currentWeather = newCurrentWeather
                existingCityRealm.currentHourlyForecastRealm.clear()
                existingCityRealm.currentHourlyForecastRealm.addAll(hourlyForecastsRealm)
                existingCityRealm.dailyForecastRealms.clear()
                existingCityRealm.dailyForecastRealms.addAll(dailyForecastsRealm)
            }
        }
    }

    override suspend fun deleteCityForecast(cityName: String) {
        realm.write {
            val dailyForecastsRealm =
                query<DailyForecastRealm>("id BEGINSWITH[c] $0", cityName).find()
            delete(dailyForecastsRealm)

            val hourlyForecastsRealm =
                query<HourlyForecastRealm>("id BEGINSWITH[c] $0", cityName).find()
            delete(hourlyForecastsRealm)

            val currentWeatherRealm =
                query<CurrentWeatherRealm>("cityName == $0", cityName).find()
            delete(currentWeatherRealm)

            val cityRealm = query<CityRealm>("cityName == $0", cityName).find()
            delete(cityRealm)
        }
    }
}