package com.lu.weather.common.retrofit

import com.google.gson.Gson
import com.lu.unsplash.comon.retrofit.ResultCallAdapter
import com.lu.weather.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitHelper @Inject constructor() {
    /// weatherbit
    fun providesWeatherbitClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(7, TimeUnit.SECONDS)
            .readTimeout(7, TimeUnit.SECONDS)
            .writeTimeout(7, TimeUnit.SECONDS)
            .addInterceptor(WeatherbitInterceptor())
            .build()
    }

    fun provideWeatherbitRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.WEATHER_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(ResultCallAdapter())
            .client(providesWeatherbitClient())
            .build()
    }

//    fun providesOpenWeatherMapClient(): OkHttpClient {
//        return OkHttpClient.Builder()
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .readTimeout(10, TimeUnit.SECONDS)
//            .writeTimeout(10, TimeUnit.SECONDS)
//            .addInterceptor(OpenWeatherMapInterceptor())
//            .build()
//    }
//
//    fun providesOpenWeatherMapRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(openWeatherMapUrl)
//            .addConverterFactory(GsonConverterFactory.create(Gson()))
//            .addCallAdapterFactory(ResultCallAdapter())
//            .client(providesOpenWeatherMapClient())
//            .build()
//    }
}