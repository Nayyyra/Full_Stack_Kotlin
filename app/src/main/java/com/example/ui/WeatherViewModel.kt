package com.example.ui

import androidx.lifecycle.*
import com.example.data.WeatherRepository
import kotlinx.coroutines.launch

//Esta clase se encarga de manejar los datos del clima para la pantalla

//Recibimos como parámetro el repositorio que es el que obtiene los datos de la API o de la bbdd local
class WeatherViewModel(private val repo: WeatherRepository) : ViewModel() {

    //Guardamos en weather los datos que nos da el repositorio sobre el clima actualizado
    val weather = repo.weather.asLiveData()

    fun load(lat: Double, lon: Double) {
        viewModelScope.launch {
            //Refrescamos los datos del clima según la ubicación
            repo.refreshWeather(lat, lon)
        }
    }
}
