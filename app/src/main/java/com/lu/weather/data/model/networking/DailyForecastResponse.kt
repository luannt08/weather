package com.lu.weather.data.model.networking

import com.google.gson.annotations.SerializedName

data class DailyForecastResponse(
    @field:SerializedName("data")
    val days: List<DailyWeatherResponse>,

    @field:SerializedName("lat")
    val lat: Double,

    @field:SerializedName("lon")
    val lon: Double,

    @field:SerializedName("timezone")
    val timeZone: String,

    @field:SerializedName("city_name")
    val cityName: String
)

data class DailyWeatherResponse(
    @field:SerializedName("high_temp")
    val highTemperature: Float,

    @field:SerializedName("low_temp")
    val lowTemperature: Float,

    @field:SerializedName("pop")
    val chanceOfRain: Int,

    @field:SerializedName("ts")
    val timeStamp: Long,

    @field:SerializedName("weather")
    val weatherCondition: WeatherCondition,
)