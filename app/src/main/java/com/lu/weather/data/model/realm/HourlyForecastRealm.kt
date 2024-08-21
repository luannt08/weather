package com.lu.weather.data.model.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class HourlyForecastRealm : RealmObject {
    @PrimaryKey
    var id: String = ""
    var chanceOfRain: Int = 0
    var timeStamp: Long = 0L
    var timeZone: String = ""
    var temperature: Int = 0
    var icon: String = ""
    var weatherCondition: String = ""
    var code: Int = 0
    var isDay:Boolean = true
}