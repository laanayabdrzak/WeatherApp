package com.laanayabdrzak.weatheapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherData: List<WeatherEntity>)

    @Query("SELECT * FROM weather_data")
    suspend fun getWeatherData(): List<WeatherEntity>
}