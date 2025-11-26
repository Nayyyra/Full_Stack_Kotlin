package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

//Tabla de la bbdd de Room donde guardaremos las ciudades favoritas
@Entity(
    //En la tabla favourite_cities
    tableName = "favorite_cities",
    //Establecemos que el nombre de la ciudad debe ser único
    //Es decir, no se puede guardar dos veces la misma ciudad
    //Esto hace que no haya ciudades duplicadas en la lista
    //de ciudades favoritas
    indices = [Index(value = ["name"], unique = true)]
)
//Creamos una clase de datos que guarda los datos de cada ciudad favorita
data class FavoriteCity(
    //Id autogenerado
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //Nombre de la ciudad
    val name: String,
    //Coordenadas de la ciudad (para poder cargar luego el tiempo de esa ciudad)
    val lat: Double,
    val lon: Double
)
