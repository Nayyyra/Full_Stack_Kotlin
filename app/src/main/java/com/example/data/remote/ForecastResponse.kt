package com.example.data.remote

//Utilizamos moshi, que es una librería para convertir
//JSON recibidos desde una API a objetos kotlin
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//Crea un adaptador para poder gestionar los datos JSON
@JsonClass(generateAdapter = true)
//Clase de datos que almacena la información principal del clima
data class MainForecastData(
    //Obtiene temperatura
    @Json(name = "temp") val temp: Double,
    //Obtiene sensación térmica
    @Json(name = "feels_like") val feelsLike: Double
)