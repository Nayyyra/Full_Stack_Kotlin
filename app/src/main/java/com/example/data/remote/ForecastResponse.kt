package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class MainForecastData(
    @Json(name = "temp") val temp: Double,
    //@Json(name = "feelslike") val feelslike: Double? = null
)