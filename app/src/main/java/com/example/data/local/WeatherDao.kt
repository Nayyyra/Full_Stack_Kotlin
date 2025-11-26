package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

//Esta interfaz es un DAO de Room
//(objeto que nos permite acceder a las bbdd y hacer operaciones en ellas)
//y contiene los métodos para leer y escribir datos en la bbdd
@Dao
//Esta interfaz es la utilizada para la tabla weather_cache
interface WeatherDao {
    //Obtenemos el clima en caché con una consulta sql
    @Query("SELECT * FROM weather_cache WHERE id = 1")
    //Devuelve un flow de kotlin (forma de recibir datos en kotlin que permite recibir
    //grandes cantidades de datos y actuar en consencuencia a ellos)
    //Cada vez que los datos cambien en la bbdd los observadores recibirán la actualización
    fun getCachedWeather(): Flow<CachedWeather?>

    //Método que inserta un registro en la bbdd
    //Si ya existe un recurso con el mismo id lo reemplaza en vez de lanzar un error
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //Función suspendida que se ejecuta dentro de una coroutine
    //(las coroutine son basicamente tareas en segundo plano)
    //(no se puede llamar desde código normal)
    //y permite que la inserción de datos se ejecute en segundo plano)
    suspend fun insertWeather(weather: CachedWeather)


}