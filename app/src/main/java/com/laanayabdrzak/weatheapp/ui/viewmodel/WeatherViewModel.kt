package com.laanayabdrzak.weatheapp.ui.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laanayabdrzak.weatheapp.data.local.WeatherDao
import com.laanayabdrzak.weatheapp.data.model.WeatherEntity
import com.laanayabdrzak.weatheapp.domain.WeatherRepository
import com.laanayabdrzak.weatheapp.data.model.WeatherData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository,
                                           private val weatherDao: WeatherDao,
                                           @ApplicationContext private val context: Context) : ViewModel() {

    private var _weatherData = MutableLiveData<WeatherDataState>()
    val weatherData: LiveData<WeatherDataState> get() = _weatherData

    private var _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private var _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun fetchWeatherData() {
        viewModelScope.launch {
            _weatherData.value = WeatherDataState.Loading(true)
            try {
                if (isNetworkAvailable()) {
                    val data = repository.getWeatherData()
                    _weatherData.value = WeatherDataState.Success(data)
                    saveWeatherDataToDatabase(data)
                } else {
                    val localData = retrieveWeatherDataFromDatabase()
                    if (localData != null) {
                        _weatherData.value = WeatherDataState.Success(localData)
                    } else {
                        _weatherData.value = WeatherDataState.Error("No data retrieved")
                    }
                }
            } catch (e: Exception) {
                handleException(e)
            } finally {
                _weatherData.value = WeatherDataState.Loading(false)
            }
        }
    }
    private suspend fun retrieveWeatherDataFromDatabase(): WeatherData? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val weatherEntities = weatherDao.getWeatherData()
                weatherEntities?.takeIf { it.isNotEmpty() }?.run {
                    val weatherDataDetails = WeatherData.WeatherDataDetails(
                        timelines = listOf(
                            WeatherData.WeatherDataDetails.Timeline(
                                timestep = "1h",
                                startTime = "",
                                endTime = "",
                                intervals = weatherEntities.map {
                                    WeatherData.WeatherDataDetails.Timeline.Interval(
                                        startTime = "",
                                        values = WeatherData.WeatherDataDetails.Timeline.Interval.WeatherValues(
                                            temperature = it.temperature,
                                            temperatureApparent = it.temperatureApparent,
                                            windSpeed = it.windSpeed
                                        )
                                    )
                                }
                            )
                        )
                    )
                    WeatherData(data = weatherDataDetails, warnings = emptyList())
                }
            }.getOrElse {
                null
            }
        }
    }
    private fun saveWeatherDataToDatabase(weatherData: WeatherData) {
        viewModelScope.launch {
            val intervals = weatherData.data.timelines
                .find { it.timestep == "1h" }
                ?.intervals

            if (!intervals.isNullOrEmpty()) {
                val weatherEntities = intervals.map {
                    WeatherEntity(
                        temperature = it.values.temperature,
                        temperatureApparent = it.values.temperatureApparent,
                        windSpeed = it.values.windSpeed
                    )
                }

                weatherDao.insertWeatherData(weatherEntities)
            }
        }
    }

    private fun handleException(e: Exception) {
        _error.value = when (e) {
            is IOException -> "Network error: ${e.message}"
            else -> "Error: ${e.message}"
        }
    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

sealed class WeatherDataState {
    data class Success(val weatherData: WeatherData) : WeatherDataState()
    data class Error(val errorMessage: String) : WeatherDataState()
    data class Loading(val isLoading: Boolean) : WeatherDataState()
}
