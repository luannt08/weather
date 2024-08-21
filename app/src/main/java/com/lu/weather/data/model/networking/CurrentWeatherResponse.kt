package com.lu.weather.data.model.networking

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    @field:SerializedName("data")
    val data: List<WeatherResponse>
)

data class WeatherResponse(
    @field:SerializedName("city_name")
    val cityName: String,

    @field:SerializedName("country_code")
    val countryCode: String?,

    @field:SerializedName("state")
    val state: String?,

    @field:SerializedName("lat")
    val lat: Double,

    @field:SerializedName("lon")
    val lon: Double,

    @field:SerializedName("app_temp")
    val feelsLike: Float,

    @field:SerializedName("temp")
    val temperature: Float,

    @field:SerializedName("uv")
    val uv: Int,

    @field:SerializedName("rh")
    val humid: Int,

    @field:SerializedName("precip")
    val precipitation: Float,

    @field:SerializedName("timezone")
    val timeZone: String,

    @field:SerializedName("ts")
    val timeStamp: Long,

    @field:SerializedName("wind_spd")
    val winSpeed: Float,

    @field:SerializedName("sunrise")
    val sunrise: String,

    @field:SerializedName("sunset")
    val sunset: String,

    @field:SerializedName("pod")
    val pod: String,

    @field:SerializedName("weather")
    val weatherCondition: WeatherCondition,

    @field:SerializedName("aqi")
    val airQuality: Int
)