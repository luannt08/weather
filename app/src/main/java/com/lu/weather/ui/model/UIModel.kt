package com.lu.weather.ui.model

import androidx.annotation.DrawableRes

data class UIFullForecast(
    val uiCurrentWeather: UICurrentWeather?,
    val uiHourlyForecastOfCurrentDay: List<UIHourlyForecast>,
    val uiDailyForecast: List<UIDailyForecast>,
    val isLocal: Boolean
) {
    companion object {
        val EMPTY = UIFullForecast(null, listOf(), listOf(), true)
    }

    fun isEmpty(): Boolean {
        return uiCurrentWeather == null && uiHourlyForecastOfCurrentDay.isEmpty() && uiDailyForecast.isEmpty()
    }
}

data class UICurrentWeather(
    val city: String,
    val currentTime: String,
    val temperature: Int,
    val weatherCondition: String,
    val uvIndex: Int,
    val sunriseTime: String,
    val sunsetTime: String,
    val windSpeed: Float,
    val precipitation: Float,
    val humid: Int,
    val icon: Int,
    val airQuality: Int,
    val colorBg: Int
)

data class UIHourlyForecast(
    val timeInString: String,
    @DrawableRes val icon: Int,
    val chanceOfRain: Int,
    val temperature: Int
)

data class UIDailyForecast(
    val timeInDay: String,
    @DrawableRes val icon: Int,
    val chanceOfRain: Int,
    val lowTemperature: Int,
    val highTemperature: Int,
)

data class UILocation(
    val cityName: String,
    val countryCode: String
)

data class UIDeleteState(
    val isLoading: Boolean,
    val isDone: Boolean
) {
    companion object {
        val EMPTY = UIDeleteState(false, false)
    }
}

data class UIError(
    val time: Long,
    val code: Int
) {
    companion object {
        val EMPTY = UIError(0L, 0)
    }
}