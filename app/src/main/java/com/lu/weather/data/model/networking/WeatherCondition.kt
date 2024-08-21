package com.lu.weather.data.model.networking

import com.google.gson.annotations.SerializedName


data class WeatherCondition(
    @field:SerializedName("icon")
    val icon: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("code")
    val code: Int
)