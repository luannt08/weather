package com.lu.weather.data.repository.weatherbit

import com.lu.weather.data.model.networking.CurrentWeatherResponse
import com.lu.weather.data.model.networking.DailyForecastResponse
import com.lu.weather.data.model.networking.HourlyForecastResponse
import com.lu.weather.data.model.networking.LocationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPIService {

    @GET("/v2.0/current")
    suspend fun fetchCurrentWeather(
        @Query("city") city: String
    ): Result<CurrentWeatherResponse>

    @GET("/v2.0/forecast/hourly")
    suspend fun fetchHourlyForecast(
        @Query("city") city: String,
        @Query("hours") hours: Int
    ): Result<HourlyForecastResponse>

    @GET("/v2.0/forecast/daily")
    suspend fun fetchDailyForecast(
        @Query("city") city: String
    ): Result<DailyForecastResponse>

    @GET("/v2.0/current")
    suspend fun search(
        @Query("city") city: String
    ): Result<LocationResponse>
}