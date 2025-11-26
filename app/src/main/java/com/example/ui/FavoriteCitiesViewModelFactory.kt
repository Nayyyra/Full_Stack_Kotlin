package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.WeatherRepository

//Clase Factory para poder crear objetos FavoriteCitiesViewModel
class FavoriteCitiesViewModelFactory(
    //Le pasamos el repositorio
    private val repo: WeatherRepository
) : ViewModelProvider.Factory {

    //Sobreescribimos el método create
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //Si el viewModel que pide android es FavoriteCitiesViewModel
        if (modelClass.isAssignableFrom(FavoriteCitiesViewModel::class.java)) {
            //Suprimimos posibles advertencias
            @Suppress("UNCHECKED_CAST")
            //Lo crea con el repositorio que hemos pasado
            return FavoriteCitiesViewModel(repo) as T
        }
        //Sino salta una excepción
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}