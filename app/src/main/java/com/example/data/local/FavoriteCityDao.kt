package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//Interfaz que define las operaciones con la tabla de ciudades favoritas
@Dao
interface FavoriteCityDao {
    //Insertar o actualizar una ciudad favorita
    //Si ya existe una ciudad con el mismo id, Room la reemplazá por la nueva
    //pero nunca habrá dos instancias de la misma ciudad
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: FavoriteCity)

    //Obtener todas las ciudades favoritas guardadas
    @Query("SELECT * FROM favorite_cities")
    fun getAll(): Flow<List<FavoriteCity>>

    //Borrar una ciudad favorita concreta
    @Delete
    suspend fun delete(city: FavoriteCity)

    //Comprobar si ya existe una ciudad por nombre
    @Query("SELECT COUNT(*) FROM favorite_cities WHERE name = :name")
    suspend fun countByName(name: String): Int
}