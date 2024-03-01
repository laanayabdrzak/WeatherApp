package com.laanayabdrzak.weatheapp.ui.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.laanayabdrzak.weatheapp.databinding.ActivityMainBinding
import com.laanayabdrzak.weatheapp.ui.viewmodel.WeatherViewModel
import com.laanayabdrzak.weatheapp.ui.adapter.WeatherListAdapter
import com.laanayabdrzak.weatheapp.ui.viewmodel.WeatherDataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()
    private val adapter: WeatherListAdapter by lazy { WeatherListAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            listView.adapter = adapter

            swipeRefreshLayout.setOnRefreshListener(viewModel::fetchWeatherData)
        }

        viewModel.weatherData.observe(this) { state ->
            when (state) {
                is WeatherDataState.Success -> {
                    hideErrorView()
                    adapter.setData(state.weatherData.data.timelines.firstOrNull()?.intervals ?: emptyList())
                }
                is WeatherDataState.Error -> {
                    showErrorView(state.errorMessage)
                }
                is WeatherDataState.Loading -> {
                    // Handle loading visibility
                    binding.swipeRefreshLayout.isRefreshing = state.isLoading
                }
            }
        }

        viewModel.fetchWeatherData()
    }

    private fun showErrorView(errorMessage: String) {
        with(binding) {
            errorTextView.text = errorMessage
            errorTextView.visibility = TextView.VISIBLE
            swipeRefreshLayout.visibility = View.GONE
        }
    }

    private fun hideErrorView() {
        with(binding) {
            errorTextView.visibility = TextView.GONE
            swipeRefreshLayout.visibility = View.VISIBLE
        }
    }
}