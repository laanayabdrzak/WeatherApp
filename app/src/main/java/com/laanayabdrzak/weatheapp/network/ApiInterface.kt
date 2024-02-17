package com.laanayabdrzak.weatheapp.network

import com.laanayabdrzak.weatheapp.data.remote.WeatherData
import retrofit2.http.GET

interface ApiInterface {

    @GET("weather")
    suspend fun getWeatherData(): WeatherData
}