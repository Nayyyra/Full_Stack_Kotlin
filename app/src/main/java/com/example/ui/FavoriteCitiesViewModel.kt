package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.data.WeatherRepository

//Clase para gestionar la lista de ciudades favoritas en la UI
class FavoriteCitiesViewModel(private val repo: WeatherRepository) : ViewModel() {

    //Lista de ciudades favoritas que se va actualizando y se muestra en la UI
    val favoriteCities = repo.getFavoriteCities().asLiveData()
}