package com.lu.weather.data.model.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class DailyForecastRealm : RealmObject {
    @PrimaryKey
    var id: String = ""
    var lat: Double = 0.0
    var lon: Double = 0.0
    var highTemperature: Int = 0
    var lowTemperature: Int = 0
    var chanceOfRain: Int = 0
    var timeStamp: Long = 0L
    var timeZone: String = ""
    var icon: String = ""
    var weatherCondition: String = ""
    var code: Int = 0
}