package com.example.ui

import androidx.lifecycle.*
import com.example.data.WeatherRepository
import com.example.data.remote.ForecastItem
import kotlinx.coroutines.launch

//Esta clase se encarga de manejar los datos del clima para la pantalla

//Recibimos como parámetro el repositorio que es el que obtiene los datos de la API o de la bbdd local
class WeatherViewModel(private val repo: WeatherRepository) : ViewModel() {

    //Guardamos en weather los datos que nos da el repositorio sobre el clima actualizado
    val weather = repo.weather.asLiveData()

    //Método que carga el tiempo actual según ubicación
    fun load(lat: Double, lon: Double) {
        viewModelScope.launch {
            //Refrescamos los datos del clima según la ubicación
            repo.refreshWeather(lat, lon)
        }
    }

    //Creamos una variable privada (para que solo el viewModel pueda cambiarlo)
    //y que guarde la lista de pronósticos para los próximos días y horas
    private val _forecast = MutableLiveData<List<ForecastItem>>()

    //Una versión "Protegida" del _forecast para que solo se pueda leer desde fuera
    val forecast: LiveData<List<ForecastItem>> = _forecast

    //Función para obtener el pronóstico de 5 días
    fun loadForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            //Pide al repositorio el pronóstico según ubicación
            val items = repo.getFiveDayForecast(lat, lon)
            //Si la respuesta no es nula
            if (items != null) {
                //Actualiza el pronóstico
                _forecast.value = items
            }
        }
    }
}
