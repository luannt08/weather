package com.lu.weather.data.model.networking

import com.google.gson.annotations.SerializedName

data class HourlyForecastResponse(
    @field:SerializedName("data")
    val hours: List<HourlyWeatherResponse>,

    @field:SerializedName("lat")
    val lat: Double,

    @field:SerializedName("lon")
    val lon: Double,

    @field:SerializedName("timezone")
    val timeZone: String,

    @field:SerializedName("city_name")
    val cityName: String
)

data class HourlyWeatherResponse(
    @field:SerializedName("pop")
    val chanceOfRain: Int,

    @field:SerializedName("ts")
    val timeStamp: Long,

    @field:SerializedName("temp")
    val temperature: Float,

    @field:SerializedName("weather")
    val weatherCondition: WeatherCondition,

    @field:SerializedName("pod")
    val pod: String
)