package com.lu.weather.common.retrofit

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class OpenWeatherMapInterceptor : Interceptor {
    companion object {
        private const val APP_ID = "appid"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
        val newUrl = request.url().newBuilder()
            .addQueryParameter(APP_ID, "0558de92930fd7820a0dabddc7bca32c").build()
        newRequest.url(newUrl)

        Log.d("OpenWeatherMapInterceptor", "new url: ${newUrl.url()}")
        return chain.proceed(newRequest.build())
    }
}