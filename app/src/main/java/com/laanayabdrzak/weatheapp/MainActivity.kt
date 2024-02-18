package com.laanayabdrzak.weatheapp

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.laanayabdrzak.weatheapp.data.local.WeatherDao
import com.laanayabdrzak.weatheapp.data.local.WeatherDatabase
import com.laanayabdrzak.weatheapp.data.local.WeatherEntity
import com.laanayabdrzak.weatheapp.data.remote.WeatherData
import com.laanayabdrzak.weatheapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var listView: ListView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var errorTextView: TextView

    private lateinit var weatherDao: WeatherDao
    private val weatherRepository: WeatherRepository by lazy {
        WeatherRepository()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        listView = binding.listView
        swipeRefreshLayout = binding.swipeRefreshLayout
        errorTextView = binding.errorTextView

        initDatabase()
        setupSwipeRefreshLayout()

        launch {
            try {
                if (isNetworkAvailable()) {
                    val weatherData = weatherRepository.getWeatherData()
                    displayWeatherData(weatherData)
                    saveWeatherDataToDatabase(weatherData)
                } else {
                    // Device is offline, display data from the local database
                    val localWeatherData = retrieveWeatherDataFromDatabase()
                    if (localWeatherData != null) {
                        displayWeatherData(localWeatherData)
                    } else {
                        showErrorView()
                    }
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun initDatabase() {
        weatherDao = Room.databaseBuilder(applicationContext, WeatherDatabase::class.java, "weather_database")
            .build()
            .weatherDao()
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            launch {
                try {
                    val weatherData = retrieveWeatherDataFromDatabase() ?: weatherRepository.getWeatherData()
                    displayWeatherData(weatherData)
                } catch (e: Exception) {
                    handleException(e)
                } finally {
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    private suspend fun retrieveWeatherDataFromDatabase(): WeatherData? = withContext(Dispatchers.IO) {
        runCatching {
            val weatherEntities = weatherDao.getWeatherData()
            weatherEntities?.takeIf { it.isNotEmpty() }?.run {
                val weatherDataDetails = WeatherData.WeatherDataDetails(
                    timelines = listOf(
                        WeatherData.WeatherDataDetails.Timeline(
                            timestep = "1h",
                            startTime = "",  // Set a suitable startTime here
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
            // Log or handle the exception if needed
            null
        }
    }

    private fun saveWeatherDataToDatabase(weatherData: WeatherData) {
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

            launch {
                weatherDao.insertWeatherData(weatherEntities)
            }
        }
    }

    private fun displayWeatherData(weatherData: WeatherData) {
        val data = weatherData.data.timelines
            .find { it.timestep == "1h" }
            ?.intervals

        if (!data.isNullOrEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data.map { interval ->
                "Température: ${interval.values.temperature}\n" +
                        "Température apparent: ${interval.values.temperatureApparent}\n" +
                        "Vitesse du vent: ${interval.values.windSpeed}"
            })
            listView.adapter = adapter
            hideErrorView()
            saveWeatherDataToDatabase(weatherData)
        } else {
            showErrorView()
        }
    }

    private fun handleException(e: Exception) {
        // Handle different types of exceptions here
        showErrorView()
        // You can log the exception or show a more specific error message based on the exception type.
        // For example, you can check if the exception is a network-related exception and show a specific error message.
    }

    private fun showErrorView() {
        errorTextView.visibility = TextView.VISIBLE
        swipeRefreshLayout.visibility = View.GONE
    }

    private fun hideErrorView() {
        errorTextView.visibility = TextView.GONE
        swipeRefreshLayout.visibility = View.VISIBLE
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}