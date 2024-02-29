package com.laanayabdrzak.weatheapp

import com.laanayabdrzak.weatheapp.data.model.WeatherData
import com.laanayabdrzak.weatheapp.data.remote.WeatherRepository
import com.laanayabdrzak.weatheapp.network.ApiInterface
import com.laanayabdrzak.weatheapp.network.RetrofitClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherRepositoryTest {

    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setUp() {
        // Mock ApiInterface
        val mockApiInterface = mockk<ApiInterface>()
        RetrofitClient.weatherApiService = mockApiInterface

        // Initialize WeatherRepository
        weatherRepository = WeatherRepository()
    }

    @Test
    fun `getWeatherData success`() = runTest {
        // Given
        val expectedWeatherData = createMockWeatherData()
        coEvery { RetrofitClient.weatherApiService.getWeatherData() } returns expectedWeatherData

        // When
        val result = weatherRepository.getWeatherData()

        // Then
        assertEquals(expectedWeatherData, result)
    }

    @Test(expected = Exception::class)
    fun `getWeatherData failure`() = runBlockingTest {
        // Given
        coEvery { RetrofitClient.weatherApiService.getWeatherData() } throws Exception("Network error")

        // When
        weatherRepository.getWeatherData()

        // Exception is expected
    }

    private fun createMockWeatherData(): WeatherData {
        // Create and return a mock WeatherData object for testing
        // You can customize this based on your actual response structure
        // Assuming you have a string representing your JSON data
        val jsonString = "{\n" +
                "    \"data\": {\n" +
                "        \"timelines\": [\n" +
                "            {\n" +
                "                \"timestep\": \"1h\",\n" +
                "                \"startTime\": \"2021-03-24T14:47:00-04:00\",\n" +
                "                \"endTime\": \"2021-03-25T14:47:00-04:00\",\n" +
                "                \"intervals\": [{\n" +
                "                        \"startTime\": \"2021-03-24T14:47:00-04:00\",\n" +
                "                        \"values\": {\n" +
                "                            \"precipitationIntensity\": 0.0083,\n" +
                "                            \"precipitationType\": 1,\n" +
                "                            \"windSpeed\": 3,\n" +
                "                            \"windGust\": 6.98,\n" +
                "                            \"windDirection\": 21,\n" +
                "                            \"temperature\": 56.98,\n" +
                "                            \"temperatureApparent\": 55.87,\n" +
                "                            \"cloudCover\": 100,\n" +
                "                            \"cloudBase\": null,\n" +
                "                            \"cloudCeiling\": null,\n" +
                "                            \"weatherCode\": 4000\n" +
                "                        }\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"warnings\": [{\n" +
                "        \"code\": 246009,\n" +
                "        \"type\": \"Missing Time Range\",\n" +
                "        \"message\": \"The timestep is not supported in full for the time range requested.\",\n" +
                "        \"meta\": {\n" +
                "            \"timestep\": \"current\",\n" +
                "            \"from\": \"now\",\n" +
                "            \"to\": \"+1m\"\n" +
                "        }\n" +
                "    }]\n" +
                "}\n"

        // Use Moshi to parse the JSON string into your data classes
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(WeatherData::class.java)
        val weatherData = adapter.fromJson(jsonString)

        return weatherData!!
    }
}
