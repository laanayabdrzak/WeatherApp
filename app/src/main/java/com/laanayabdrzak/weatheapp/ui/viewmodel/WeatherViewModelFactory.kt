package com.laanayabdrzak.weatheapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laanayabdrzak.weatheapp.data.local.WeatherDao
import com.laanayabdrzak.weatheapp.data.remote.WeatherRepository

class WeatherViewModelFactory(private val repository: WeatherRepository,
                              private val weatherDao: WeatherDao,
                              private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(repository, weatherDao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
