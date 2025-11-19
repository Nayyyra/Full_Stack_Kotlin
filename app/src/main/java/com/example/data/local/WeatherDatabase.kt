package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

//La anotación Database le dice a Room qué tablas tendrá la bbdd usando "entities="
//Indica tb la versión de la bbdd
//Si debe importar un archivo de esquema (solo útil en proyectos grandes)
@Database(entities = [CachedWeather::class], version = 2, exportSchema = false)
//Esto convierte a la clase en la bbdd que Room generará
abstract class WeatherDatabase : RoomDatabase() {
    //Obtenemos el DAO, que es el objeto que nos permite acceder a la bbdd
    abstract fun dao(): WeatherDao
}