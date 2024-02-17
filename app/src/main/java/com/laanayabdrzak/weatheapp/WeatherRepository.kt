package com.laanayabdrzak.weatheapp

import com.laanayabdrzak.weatheapp.data.remote.WeatherData
import com.laanayabdrzak.weatheapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository {

    suspend fun getWeatherData(): WeatherData = withContext(Dispatchers.IO) {
        try {
            RetrofitClient.weatherApiService.getWeatherData()
        } catch (e: Exception) {
            // Handle the exception (e.g., network error)
            throw e
        }
    }
}