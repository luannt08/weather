package com.lu.weather.common.retrofit

import android.util.Log
import com.lu.weather.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class WeatherbitInterceptor : Interceptor {
    companion object {
        private const val KEY = "key"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
        val newUrl = request.url().newBuilder()
            .addQueryParameter(KEY, BuildConfig.API_KEY).build()
        newRequest.url(newUrl)
        Log.d("WeatherbitInterceptor", "url: $newUrl")

        return chain.proceed(newRequest.build())
    }
}