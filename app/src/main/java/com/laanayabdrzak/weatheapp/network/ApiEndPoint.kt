package com.laanayabdrzak.weatheapp.network

import com.laanayabdrzak.weatheapp.data.model.WeatherData
import retrofit2.Response
import retrofit2.http.GET

interface ApiEndPoint {

    @GET("weather")
    suspend fun getWeatherData(): Response<WeatherData>
}