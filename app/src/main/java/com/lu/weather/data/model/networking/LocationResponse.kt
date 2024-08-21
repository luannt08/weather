package com.lu.weather.data.model.networking

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @field:SerializedName("data")
    val data: List<ItemResponse>
)

data class ItemResponse(
    @field:SerializedName("city_name")
    val cityName: String,

    @field:SerializedName("country_code")
    val countryCode: String?,

    @field:SerializedName("state")
    val state: String?,

    @field:SerializedName("lat")
    val lat: Double,

    @field:SerializedName("lon")
    val lon: Double
)
