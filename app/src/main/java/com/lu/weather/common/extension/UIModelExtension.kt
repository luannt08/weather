package com.lu.weather.common.extension

import com.lu.weather.data.model.CurrentWeatherData
import com.lu.weather.data.model.DailyForecastData
import com.lu.weather.data.model.HourlyForecastData
import com.lu.weather.ui.EEE
import com.lu.weather.ui.HH
import com.lu.weather.ui.HHmm
import com.lu.weather.ui.codeToBackgroundColor
import com.lu.weather.ui.codeToDrawable
import com.lu.weather.ui.model.UICurrentWeather
import com.lu.weather.ui.model.UIDailyForecast
import com.lu.weather.ui.model.UIHourlyForecast
import com.lu.weather.ui.timeStampToTimeWithFormat
import com.lu.weather.ui.utcStringToTime

fun CurrentWeatherData.dataToUICurrentWeather(city: String, timeZone: String) = UICurrentWeather(
    city,
    timeStampToTimeWithFormat(timeStamp, timeZone, HHmm),
    temperature,
    weatherCondition,
    uv,
    sunrise.utcStringToTime(timeZone, HHmm),
    sunset.utcStringToTime(timeZone, HHmm),
    windSpeed,
    precipitation,
    humid,
    iconCode.codeToDrawable(isDay),
    airQuality,
    iconCode.codeToBackgroundColor(isDay)
)

fun HourlyForecastData.dataToUIHourlyForecast() =
    UIHourlyForecast(
        timeStampToTimeWithFormat(timeStamp, timeZone, HH),
        code.codeToDrawable(isDay),
        chanceOfRain,
        temperature
    )

fun DailyForecastData.dataToUIDailyForecast() =
    UIDailyForecast(
        timeStampToTimeWithFormat(timeStamp, timeZone, EEE),
        code.codeToDrawable(true),
        chanceOfRain,
        lowTemperature,
        highTemperature
    )