package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//Aqui definimos las data class que respresentan las respuestas JSON de la API
//y que usan Moshi para deserializar automáticamente los datos

//Indicamos a moshi que genere un adaptador que pueda
//convertir JSON a kotlin y viceverdas
@JsonClass(generateAdapter = true)
//Respuesta sobre el clima principal
data class WeatherResponse(
    //El campo name (nombre de la ciudad) lo mapeamos a la variable city
    @Json(name = "name") val city: String,
    //El campo main (info del clima principal) lo mapeamos a la clase MainData
    @Json(name = "main") val main: MainData,
    //El array de objetos weather (descripción del clima) lo mapeamos a una lista de WheatherDescription
    @Json(name = "weather") val weather: List<WeatherDescription>
)

@JsonClass(generateAdapter = true)
//Sección main del JSON
data class MainData(
    //El campo temp (temperatura) lo mapeamos a la variable temp
    @Json(name = "temp") val temp: Double,
    //El campo feelslike (sensación térmica) lo mapeamos a la variable feelslike
    @Json(name = "feels_like") val feelsLike: Double
)

@JsonClass(generateAdapter = true)
//Esta data class representa cada objeto dentro del array weather del JSON
data class WeatherDescription(
    //El campo descripcion (descripción del clima) lo mapeamos a la variable description
    @Json(name = "description") val description: String,
    //El campo icono (icono representativo del clima) lo mapeamos a la variable icon
    @Json(name = "icon") val icon: String
)