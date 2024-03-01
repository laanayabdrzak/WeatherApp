package com.laanayabdrzak.weatheapp.di

import android.content.Context
import com.laanayabdrzak.weatheapp.data.local.WeatherDao
import com.laanayabdrzak.weatheapp.domain.WeatherRepository
import com.laanayabdrzak.weatheapp.ui.viewmodel.WeatherViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideWeatherViewModel(
        repository: WeatherRepository,
        weatherDao: WeatherDao,
        @ApplicationContext context: Context
    ): WeatherViewModel {
        return WeatherViewModel(repository, weatherDao, context)
    }
}