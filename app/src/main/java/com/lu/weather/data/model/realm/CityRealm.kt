package com.lu.weather.data.model.realm

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class CityRealm : RealmObject {
    @PrimaryKey
    var cityName: String = ""
    var timeZone: String = ""
    var state: String = ""
    var countryCode: String = ""
    var currentWeather: CurrentWeatherRealm? = null
    var currentHourlyForecastRealm: RealmList<HourlyForecastRealm> = realmListOf()
    var dailyForecastRealms: RealmList<DailyForecastRealm> = realmListOf()
}





