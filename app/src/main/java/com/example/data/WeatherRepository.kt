package com.example.data

import android.util.Log
import com.example.data.local.CachedWeather
import com.example.data.local.WeatherDao
import com.example.data.remote.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

//Esta clase centraliza el acceso tanto a los datos locales (Room) como a los datos remotos (API)
class WeatherRepository(
    //dao para acceder a la bbdd local (Room)
    private val dao: WeatherDao,
    //api para hacer peticiones a la API de OpenWeatherMap
    private val api: WeatherApiService,
    //Mi apikey para autenticar las solicitudes
    private val apiKey: String
) {

    //Flow CachedWeather es un flujo de datos de Kotlin Coroutines
    //que genera los cambios automáticamente
    //Esto permite que la UI se actualice automáticamente cuando cambien los datos locales
    val weather: Flow<CachedWeather?> = dao.getCachedWeather()

    //Función para refrescar el clima (con suspend se ejecuta en segundo plano)
    //Latitud y longitud de la ubicación como parámetros para pedir el clima
    suspend fun refreshWeather(lat: Double, lon: Double) {
        try {
            //Llamamos al endpoint de la API usando Retrofit para obtener el clima actual
            val response = api.getCurrentWeather(lat, lon, apiKey)
            //Si la respuesta es exitosa
            if (response.isSuccessful && response.body() != null) {
                //Obtenemos los datos de la respuesta con .body()
                //y los guardamos en networkWeather
                val networkWeather = response.body()!!

                //Convertimos la respuesta en datos locales
                //Creamos la variable cachedData que guardará el objeto CachedWeather
                //que contiene la información del clima actual
                val cachedData = CachedWeather(
                    //Ciudad
                    cityName = networkWeather.city,
                    //Temperatura
                    temperature = networkWeather.main.temp,
                    //Sensación térmica
                    feelsLike = networkWeather.main.feelsLike,
                    //Descripción del clima (si es nulo pondrá N/A)
                    description = networkWeather.weather.firstOrNull()?.description ?: "N/A",
                    //Hora actual en milisegundos
                    lastUpdated = System.currentTimeMillis()
                )

                //Guardamos los datos en la bbdd
                //Usamos un pool de hilos de lectura y escritura (IO) en vez de
                //usar el hilo main para escribir los datos en la bbdd
                withContext(Dispatchers.IO) {
                    //Insertamos los datos del clima actual en la tabla weather_cache
                    dao.insertWeather(cachedData)
                }
            }
        } catch (e: Exception) {
            //Manejamos posibles errores y los registramos en un log
            Log.e("WeatherRepository", "Error refreshing weather", e)
        }
    }

}
