package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

//La anotación Database le dice a Room qué tablas tendrá la bbdd usando "entities="
//Indica tb la versión de la bbdd
//Si debe importar un archivo de esquema (solo útil en proyectos grandes)
//IMPORTANTE: Room exige que cada vez que se hagan cambios en la bbdd se debe sumar +1 a la versión de la bbdd
//Si esto no se hace, es muy posible que al ejecutar la app crashee o nos salgan errores

//Creamos la bbdd de room que tiene las tablas de los datos del clima y de las ciudades favoritas
@Database(
    entities = [CachedWeather::class,FavoriteCity::class],version = 5,exportSchema = false)

//Esto convierte a la clase en la bbdd que Room generará
abstract class WeatherDatabase : RoomDatabase() {
    //Obtenemos el DAO, que es el objeto que nos permite acceder a la bbdd
    abstract fun dao(): WeatherDao

    //DAO para acceder a la tabla de ciudades favoritas
    abstract fun favoriteCityDao(): FavoriteCityDao
}