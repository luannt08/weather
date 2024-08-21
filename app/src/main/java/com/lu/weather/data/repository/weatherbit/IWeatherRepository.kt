package com.lu.weather.data.repository.weatherbit

import com.lu.weather.data.model.CityData
import com.lu.weather.data.model.LocationData
import com.lu.weather.data.model.networking.NetworkingError
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun fetchAllCitiesForecast() // fetch from server then update to Realm

    fun getFocusingCityFlow(cityName: String): Flow<Pair<CityData, Boolean>> // boolean represent for from local or server

    fun getCitiesFlow(): Flow<List<CityData>>

    fun getErrorFlow() : Flow<NetworkingError>

    suspend fun addFocusingCityForecastToRealm()

    suspend fun deleteCityForecast(cityName: String)

    suspend fun searchCity(query: String) : List<LocationData>
}