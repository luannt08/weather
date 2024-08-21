package com.lu.weather.data.model

data class DailyForecastData (
    val id: String,
    val highTemperature: Int,
    val lowTemperature: Int,
    val chanceOfRain: Int,
    val timeStamp: Long,
    val timeZone: String,
    val icon: String,
    val weatherCondition: String,
    val code: Int = 0
)