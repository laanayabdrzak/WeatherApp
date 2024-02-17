package com.laanayabdrzak.weatheapp.data.remote


data class WeatherData(
    val data: WeatherDataDetails,
    val warnings: List<Warning>
) {
    data class WeatherDataDetails(
        val timelines: List<Timeline>
    ) {
        data class Timeline(
            val timestep: String,
            val startTime: String,
            val endTime: String,
            val intervals: List<Interval>
        ) {
            data class Interval(
                val startTime: String,
                val values: WeatherValues
            ) {
                data class WeatherValues(
                    val windSpeed: Double,
                    val temperature: Double,
                    val temperatureApparent: Double,
                )
            }
        }
    }
}




data class Warning(
    val code: Int,
    val type: String,
    val message: String,
    val meta: WarningMeta
) {
    data class WarningMeta(
        val timestep: String,
        val from: String,
        val to: String
    )
}

