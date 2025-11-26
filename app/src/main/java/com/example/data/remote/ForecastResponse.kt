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
    @Json(name = "feels_like") val feelsLike: Double,
    //Obtiene presión
    @Json(name = "pressure") val pressure: Int,
    //Obtiene humedad
    @Json(name = "humidity") val humidity: Int
)

@JsonClass(generateAdapter = true)
//Clase de datos que almacena los datos del pronóstico de 5 días
data class FiveDayForecastResponse(
    //Obtiene la lista de items del pronóstico
    @Json(name = "list") val list: List<ForecastItem>,
    //Obtiene el nombre de la ciudad
    @Json(name = "city") val city: CityInfo
)

@JsonClass(generateAdapter = true)
//Clase de datos que almacena los items del pronóstico de un momento en concreto
data class ForecastItem(
    //Obtiene el día y la hora del pronóstico
    @Json(name = "dt_txt") val dateTime: String,
    //Obtenemos campos como la temperatura, sensación térmica, presión y humedad
    @Json(name = "main") val main: MainData,
    //Obtenemos la descripción del tiempo
    @Json(name = "weather") val weather: List<WeatherDescription>
)

@JsonClass(generateAdapter = true)
//Clase de datos que contiene la información de la ciudad
data class CityInfo(
    //Obtiene el nombre de la ciudad
    @Json(name = "name") val name: String
)