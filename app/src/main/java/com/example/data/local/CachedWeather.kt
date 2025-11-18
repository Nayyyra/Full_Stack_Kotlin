package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class CachedWeather(
    @PrimaryKey val id: Int = 1,
    val cityName: String,
    val temperature: Double,
    //val feelslike : Double,
    val description: String,
    val lastUpdated: Long
)