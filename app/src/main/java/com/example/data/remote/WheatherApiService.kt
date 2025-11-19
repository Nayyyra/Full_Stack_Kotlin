package com.example.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//Esto es una interfaz de Retrofit que gestiona las peticiones HTTP que se hacen a la API
interface WeatherApiService {
    //Definimos el endpoint(punto de acceso a la API) para hacer una petición GET
    //Va a la ruta wheather dentro de la URL base
    //Es decir: https://api.openweathermap.org/data/2.5/weather
    @GET("weather")
    //Función para obtener el clima actual en segundo plano (suspend)
    //de forma que no bloquee la UI
    suspend fun getCurrentWeather(
        //Parámetros que se piden
        //Latitud de la ubicación
        @Query("lat") latitude: Double,
        //Longitud
        @Query("lon") longitude: Double,
        //La APIKEY
        @Query("appid") apiKey: String,
        //Que el idioma por defecto de la respuesta sea español
        @Query("lang") lang: String = "es",
        //Unidades del clima (celsius=metric)
        @Query("units") units: String = "metric"
        //Recibimos respuesta de tipo WheatherResponse
        //envuelta en Response para manejar errores
    ): Response<WeatherResponse>



}
