package com.lu.weather.common.realm

import com.lu.weather.data.model.realm.CityRealm
import com.lu.weather.data.model.realm.CurrentWeatherRealm
import com.lu.weather.data.model.realm.DailyForecastRealm
import com.lu.weather.data.model.realm.HourlyForecastRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealmHelper @Inject constructor() {
    fun createRealm(): Realm {
        val config = RealmConfiguration.create(
            schema = setOf(
                CityRealm::class,
                CurrentWeatherRealm::class,
                DailyForecastRealm::class,
                HourlyForecastRealm::class
            )
        )

        return Realm.open(config)
    }
}