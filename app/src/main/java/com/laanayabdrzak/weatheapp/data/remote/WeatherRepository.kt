package com.laanayabdrzak.weatheapp.data.remote

import com.laanayabdrzak.weatheapp.network.ApiInterface
import retrofit2.Response
import java.io.IOException

class WeatherRepository(private val weatherApi: ApiInterface) {

    suspend fun getWeatherData(): WeatherData {
        return try {
            val response = weatherApi.getWeatherData()
            handleResponse(response)
        } catch (e: Exception) {
            throw handleException(e)
        }
    }

    private fun handleResponse(response: Response<WeatherData>): WeatherData {
        if (response.isSuccessful) {
            return response.body() ?: throw IOException("Empty response body")
        } else {
            throw IOException("Network error: ${response.code()} ${response.message()}")
        }
    }

    private fun handleException(exception: Exception): Exception {
        return when (exception) {
            is IOException -> IOException("Network error: ${exception.message}", exception)
            else -> exception
        }
    }
}
