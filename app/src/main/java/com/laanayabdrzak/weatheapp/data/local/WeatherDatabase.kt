package com.laanayabdrzak.weatheapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.laanayabdrzak.weatheapp.data.model.WeatherEntity

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}