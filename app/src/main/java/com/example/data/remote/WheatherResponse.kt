package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "name") val city: String,
    @Json(name = "main") val main: MainData,
    @Json(name = "weather") val weather: List<WeatherDescription>
)

@JsonClass(generateAdapter = true)
data class MainData(
    @Json(name = "temp") val temp: Double,
    //@Json(name = "feelslike") val feelslike: Double
)

@JsonClass(generateAdapter = true)
data class WeatherDescription(
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
)