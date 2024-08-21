package com.lu.weather.data.model

data class HourlyForecastData(
    val id: String,
    val chanceOfRain: Int,
    val timeStamp: Long,
    val timeZone: String,
    val temperature: Int,
    val icon: String,
    val description: String,
    val code: Int,
    val isDay: Boolean
)