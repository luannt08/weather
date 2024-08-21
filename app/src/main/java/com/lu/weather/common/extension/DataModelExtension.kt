package com.lu.weather.common.extension

import com.lu.weather.data.model.CityData
import com.lu.weather.data.model.CurrentWeatherData
import com.lu.weather.data.model.DailyForecastData
import com.lu.weather.data.model.HourlyForecastData
import com.lu.weather.data.model.realm.CurrentWeatherRealm
import com.lu.weather.data.model.realm.DailyForecastRealm
import com.lu.weather.data.model.realm.HourlyForecastRealm
import com.lu.weather.data.model.networking.DailyWeatherResponse
import com.lu.weather.data.model.networking.WeatherResponse
import com.lu.weather.data.model.networking.HourlyWeatherResponse
import com.lu.weather.data.model.realm.CityRealm

/*From server model to data model*/
fun DailyWeatherResponse.toDailyForecastData(cityName: String, timeZone: String) =
    DailyForecastData(
        forecastId(cityName, timeStamp),
        highTemperature.toInt(),
        lowTemperature.toInt(),
        chanceOfRain,
        timeStamp,
        timeZone,
        weatherCondition.icon,
        weatherCondition.description,
        weatherCondition.code
    )

fun HourlyWeatherResponse.toHourlyForecastData(cityName: String, timeZone: String) =
    HourlyForecastData(
        forecastId(cityName, timeStamp),
        chanceOfRain,
        timeStamp,
        timeZone,
        temperature.toInt(),
        weatherCondition.icon,
        weatherCondition.description,
        weatherCondition.code,
        pod == "d"
    )

fun WeatherResponse.toCurrentForecastData() = CurrentWeatherData(
    cityName,
    feelsLike.toInt(),
    temperature.toInt(),
    uv,
    humid,
    precipitation,
    timeStamp,
    weatherCondition.description,
    winSpeed,
    sunrise,
    sunset,
    pod == "d",
    airQuality,
    weatherCondition.code
)/*------------------*/

/*From server model to realm model*/
fun DailyWeatherResponse.responseToDailyForecastRealm(cityName: String, timeZone: String) =
    DailyForecastRealm().apply {
        id = forecastId(cityName, this@responseToDailyForecastRealm.timeStamp)
        highTemperature = this@responseToDailyForecastRealm.highTemperature.toInt()
        lowTemperature = this@responseToDailyForecastRealm.lowTemperature.toInt()
        chanceOfRain = this@responseToDailyForecastRealm.chanceOfRain
        timeStamp = this@responseToDailyForecastRealm.timeStamp
        this.timeZone = timeZone
        icon = this@responseToDailyForecastRealm.weatherCondition.icon
        weatherCondition = this@responseToDailyForecastRealm.weatherCondition.description
        code = this@responseToDailyForecastRealm.weatherCondition.code
    }

fun HourlyWeatherResponse.responseToHourlyForecastRealm(cityName: String, timeZone: String) =
    HourlyForecastRealm().apply {
        id = forecastId(cityName, this@responseToHourlyForecastRealm.timeStamp)
        chanceOfRain = this@responseToHourlyForecastRealm.chanceOfRain
        timeStamp = this@responseToHourlyForecastRealm.timeStamp
        this.timeZone = timeZone
        temperature = this@responseToHourlyForecastRealm.temperature.toInt()
        icon = this@responseToHourlyForecastRealm.weatherCondition.icon
        weatherCondition = this@responseToHourlyForecastRealm.weatherCondition.description
        code = this@responseToHourlyForecastRealm.weatherCondition.code
        this.isDay = this@responseToHourlyForecastRealm.pod == "d"
    }

fun WeatherResponse.responseToCurrentForecastRealm() = CurrentWeatherRealm().apply {
    cityName = this@responseToCurrentForecastRealm.cityName
    feelsLike = this@responseToCurrentForecastRealm.feelsLike.toInt()
    temperature = this@responseToCurrentForecastRealm.temperature.toInt()
    uv = this@responseToCurrentForecastRealm.uv
    humid = this@responseToCurrentForecastRealm.humid
    precipitation = this@responseToCurrentForecastRealm.precipitation
    timeStamp = this@responseToCurrentForecastRealm.timeStamp
    weatherCondition = this@responseToCurrentForecastRealm.weatherCondition.description
    windSpeed = this@responseToCurrentForecastRealm.winSpeed
    sunrise = this@responseToCurrentForecastRealm.sunrise
    sunset = this@responseToCurrentForecastRealm.sunset
    isDay = this@responseToCurrentForecastRealm.pod == "d"
    airQuality = this@responseToCurrentForecastRealm.airQuality
    iconCode = this@responseToCurrentForecastRealm.weatherCondition.code
}

fun CurrentWeatherData.dataToCurrentWeatherRealm() = CurrentWeatherRealm().apply {
    this.cityName = this@dataToCurrentWeatherRealm.cityName
    this.feelsLike = this@dataToCurrentWeatherRealm.feelsLike
    this.temperature = this@dataToCurrentWeatherRealm.temperature
    this.uv = this@dataToCurrentWeatherRealm.uv
    this.humid = this@dataToCurrentWeatherRealm.humid
    this.precipitation = this@dataToCurrentWeatherRealm.precipitation
    this.timeStamp = this@dataToCurrentWeatherRealm.timeStamp
    this.weatherCondition = this@dataToCurrentWeatherRealm.weatherCondition
    this.windSpeed = this@dataToCurrentWeatherRealm.windSpeed
    this.sunrise = this@dataToCurrentWeatherRealm.sunrise
    this.sunset = this@dataToCurrentWeatherRealm.sunset
    this.isDay = this@dataToCurrentWeatherRealm.isDay
    this.airQuality = this@dataToCurrentWeatherRealm.airQuality
    this.iconCode = this@dataToCurrentWeatherRealm.iconCode
}


fun HourlyForecastData.dataToHourlyForecastRealm() = HourlyForecastRealm().apply {
    this.id = this@dataToHourlyForecastRealm.id
    this.chanceOfRain = this@dataToHourlyForecastRealm.chanceOfRain
    this.timeStamp = this@dataToHourlyForecastRealm.timeStamp
    this.timeZone = this@dataToHourlyForecastRealm.timeZone
    this.temperature = this@dataToHourlyForecastRealm.temperature
    this.icon = this@dataToHourlyForecastRealm.icon
    this.weatherCondition = this@dataToHourlyForecastRealm.description
    this.code = this@dataToHourlyForecastRealm.code
    this.isDay = this@dataToHourlyForecastRealm.isDay
}

fun DailyForecastData.dataToDailyForecastRealm() = DailyForecastRealm().apply {
    this.id = this@dataToDailyForecastRealm.id
    this.highTemperature = this@dataToDailyForecastRealm.highTemperature
    this.lowTemperature = this@dataToDailyForecastRealm.lowTemperature
    this.chanceOfRain = this@dataToDailyForecastRealm.chanceOfRain
    this.timeStamp = this@dataToDailyForecastRealm.timeStamp
    this.timeZone = this@dataToDailyForecastRealm.timeZone
    this.icon = this@dataToDailyForecastRealm.icon
    this.weatherCondition = this@dataToDailyForecastRealm.weatherCondition
    this.code = this@dataToDailyForecastRealm.code
}


/*From realm to data model*/
fun CityRealm.realmToCityData() = CityData(
    cityName,
    timeZone,
    state,
    countryCode,
    currentWeather?.realmToCurrentWeatherData(),
    currentHourlyForecastRealm.map { it.realmToHourlyForecastData() },
    dailyForecastRealms.map { it.realmToDailyForecastData() })

fun CurrentWeatherRealm.realmToCurrentWeatherData() = CurrentWeatherData(
    cityName,
    feelsLike,
    temperature,
    uv,
    humid,
    precipitation,
    timeStamp,
    weatherCondition,
    windSpeed,
    sunrise,
    sunset,
    isDay,
    airQuality,
    iconCode
)

fun HourlyForecastRealm.realmToHourlyForecastData() = HourlyForecastData(
    id,
    chanceOfRain,
    timeStamp,
    timeZone,
    temperature,
    icon,
    weatherCondition,
    code,
    isDay
)

fun DailyForecastRealm.realmToDailyForecastData() = DailyForecastData(
    id,
    highTemperature,
    lowTemperature,
    chanceOfRain,
    timeStamp,
    timeZone,
    icon,
    weatherCondition,
    code
)

fun forecastId(cityName: String, timeStamp: Long) = "$cityName-$timeStamp"