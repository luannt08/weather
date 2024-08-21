package com.lu.weather.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lu.weather.common.WeatherApplication
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FetchAllCitiesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    //private val weatherRepository: IWeatherRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Log.d("WeatherWorkRequest", "dowork -")
        val repository = (applicationContext as WeatherApplication).weatherRepository
        repository.fetchAllCitiesForecast()
        return Result.success()
    }
}