package com.lu.weather.data.model

data class CurrentWeatherData(
    val cityName: String,
    val feelsLike: Int,
    val temperature: Int,
    val uv: Int,
    val humid: Int,
    val precipitation: Float,
    val timeStamp: Long,
    val weatherCondition: String,
    val windSpeed: Float,
    val sunrise: String,
    val sunset: String,
    val isDay: Boolean,
    val airQuality: Int,
    val iconCode : Int
)