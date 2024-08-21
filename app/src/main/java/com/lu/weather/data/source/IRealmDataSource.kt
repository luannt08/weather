package com.lu.weather.data.source

import com.lu.weather.data.model.realm.CityRealm
import com.lu.weather.data.model.realm.CurrentWeatherRealm
import com.lu.weather.data.model.realm.DailyForecastRealm
import com.lu.weather.data.model.realm.HourlyForecastRealm
import io.realm.kotlin.notifications.SingleQueryChange
import kotlinx.coroutines.flow.Flow

interface IRealmDataSource {
    fun checkAvailableOfCity(cityName: String) : Boolean

    fun getAllCitiesFlow(): Flow<List<CityRealm>>

    fun getAllCurrentWeathersFlow(): Flow<List<CurrentWeatherRealm>>

    suspend fun getCityFlow(cityName: String): Flow<SingleQueryChange<CityRealm>>

    fun getAllCitiesLocation(): List<String>

    suspend fun processDailyForecastData(newDailyData: List<DailyForecastRealm>, cityName: String)

    suspend fun processHourlyForecastData(newHourlyData: List<HourlyForecastRealm>, cityName: String)

    suspend fun processCurrentWeather(
        newCurrentWeather: CurrentWeatherRealm,
        newDailyData: List<DailyForecastRealm>,
        newHourlyData: List<HourlyForecastRealm>,
        city: String,
        timeZone: String,
        state: String,
        countryCode: String
    )

    suspend fun deleteCityForecast(city: String)
}