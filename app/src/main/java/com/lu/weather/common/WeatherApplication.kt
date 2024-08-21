package com.lu.weather.common

import android.app.Application
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.lu.weather.BuildConfig
import com.lu.weather.data.repository.weatherbit.IWeatherRepository
import com.lu.weather.work.FetchAllCitiesWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class WeatherApplication : Application() {

    @Inject
    lateinit var weatherRepository: IWeatherRepository

    override fun onCreate() {
        super.onCreate()
        enqueueWork()
    }

    private fun enqueueWork() {
        Log.e("Test", "buildConfig: ${BuildConfig.API_KEY}")

        WorkManager.getInstance(this).cancelAllWork()
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val weatherWorkRequest =
            PeriodicWorkRequestBuilder<FetchAllCitiesWorker>(
                15,
                TimeUnit.MINUTES
            ).setInitialDelay(5, TimeUnit.MINUTES).setConstraints(constraints).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "weather_data",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            weatherWorkRequest
        )
    }
}