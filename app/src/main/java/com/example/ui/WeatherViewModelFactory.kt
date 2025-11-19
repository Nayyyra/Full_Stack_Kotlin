package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.WeatherRepository

class WeatherViewModelFactory(
    //Recibimos el repositorio como parámetro, el cual sabe cargar datos del clima,
    //guardar datos y leer el almacenamiento local
    private val repo: WeatherRepository
    //Factory guarda el repositorio para dárselo al viewModel cuando lo cree
) : ViewModelProvider.Factory {

    //Esta función es llamada para crear el viewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //Comprobamos que el viewModel es el correcto
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            //Suprime una advertencia
            @Suppress("UNCHECKED_CAST")
            //Devuelves el viewModel como tipo T (genérico)
            return WeatherViewModel(repo) as T
        }
        //Si se pide un viewModel que no existe, se lanza una excepción
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}