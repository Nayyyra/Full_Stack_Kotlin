package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

//Clase de datos que guarda los datos del clima en caché
//Esta clase representa una tabla en la bbdd de Room
//El nombre la tabla es "weather_cache"
@Entity(tableName = "weather_cache")
data class CachedWeather(
    //PrimaryKey en 1 porque solo se guarda un registro (el útimo clima actualizado)
    @PrimaryKey val id: Int = 1,
    //Nombre de la ciudad
    val cityName: String,
    //Temperatura
    val temperature: Double,
    //Sensación térmica
    val feelsLike : Double,
    //Descripción del clima
    val description: String,
    //Cuando se actualizó la info por última vez
    val lastUpdated: Long
)