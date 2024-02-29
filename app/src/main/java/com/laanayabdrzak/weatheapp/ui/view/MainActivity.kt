package com.laanayabdrzak.weatheapp.ui.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.laanayabdrzak.weatheapp.data.local.WeatherDatabase
import com.laanayabdrzak.weatheapp.data.remote.WeatherRepository
import com.laanayabdrzak.weatheapp.databinding.ActivityMainBinding
import com.laanayabdrzak.weatheapp.network.RetrofitClient
import com.laanayabdrzak.weatheapp.ui.viewmodel.WeatherViewModel
import com.laanayabdrzak.weatheapp.ui.viewmodel.WeatherViewModelFactory
import com.laanayabdrzak.weatheapp.ui.adapter.WeatherListAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(
            WeatherRepository(RetrofitClient.weatherApiService),
            WeatherDatabase.getInstance(this).weatherDao(),
            applicationContext
        )
    }
    private val adapter: WeatherListAdapter by lazy { WeatherListAdapter(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listView.adapter = adapter

        viewModel.weatherData.observe(this) { data ->
            data?.let {
                adapter.setData(it.data.timelines.firstOrNull()?.intervals ?: emptyList())
            }
        }

        viewModel.error.observe(this) {
            // Handle error visibility
        }

        viewModel.loading.observe(this) { isLoading ->
            // Handle loading visibility
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchWeatherData()
        }

        viewModel.fetchWeatherData()
    }

    private fun showErrorView() {
        binding.errorTextView.visibility = TextView.VISIBLE
        binding.swipeRefreshLayout.visibility = View.GONE
    }

    private fun hideErrorView() {
        binding.errorTextView.visibility = TextView.GONE
        binding.swipeRefreshLayout.visibility = View.VISIBLE
    }
}