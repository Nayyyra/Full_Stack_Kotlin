package com.example.data

import android.util.Log
import com.example.data.local.CachedWeather
import com.example.data.local.WeatherDao
import com.example.data.remote.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherRepository(
    private val dao: WeatherDao,
    private val api: WeatherApiService,
    private val apiKey: String
) {
    val weather: Flow<CachedWeather?> = dao.getCachedWeather()
    suspend fun refreshWeather(lat: Double, lon: Double) {
        try {
            val response = api.getCurrentWeather(lat, lon, apiKey)
            if (response.isSuccessful && response.body() != null) {
                val networkWeather = response.body()!!

                val cachedData = CachedWeather(
                    cityName = networkWeather.city,
                    temperature = networkWeather.main.temp,
                    description = networkWeather.weather.firstOrNull()?.description ?: "N/A",
                    lastUpdated = System.currentTimeMillis()
                )

                withContext(Dispatchers.IO) {
                    dao.insertWeather(cachedData)
                }
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error refreshing weather", e)
        }
    }

}
