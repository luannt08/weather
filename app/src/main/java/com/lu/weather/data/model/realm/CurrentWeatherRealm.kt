package com.lu.weather.data.model.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class CurrentWeatherRealm : RealmObject {
    @PrimaryKey
    var cityName: String = ""
    var feelsLike: Int = 0
    var temperature: Int = 0
    var uv: Int = 0
    var humid: Int = 0
    var precipitation: Float = 0.0f
    var timeStamp: Long = 0L
    var weatherCondition: String = ""
    var windSpeed: Float = 0.0f
    var sunrise: String = ""
    var sunset: String = ""
    var isDay: Boolean = true
    var airQuality = 0
    var iconCode = 0
}