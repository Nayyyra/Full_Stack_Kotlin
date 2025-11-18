package com.example.ui

import androidx.lifecycle.*
import com.example.data.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repo: WeatherRepository) : ViewModel() {

    val weather = repo.weather.asLiveData()


    fun load(lat: Double, lon: Double) {
        viewModelScope.launch {
            repo.refreshWeather(lat, lon)
        }
    }
}
