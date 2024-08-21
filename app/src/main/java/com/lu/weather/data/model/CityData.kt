package com.lu.weather.data.model

data class CityData(
    val cityName: String,
    val timeZone: String,
    val state: String,
    val countryCode: String,
    val currentWeather: CurrentWeatherData?,
    val currentHourlyForecast: List<HourlyForecastData>,
    val dailyForecasts: List<DailyForecastData>
)





