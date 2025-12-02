package com.lab6.di

import com.lab6.data.api.WeatherApiService
import com.lab6.ui.viewmodel.WeatherViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    // OkHttp Client
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Weather API Service
    single<WeatherApiService> {
        get<Retrofit>().create(WeatherApiService::class.java)
    }

    // API Key - Replace with your actual API key
    single<String> {
        "8889c1feba30ba15f018e6919a6bc4e2"
    }

    // ViewModel
    viewModel { WeatherViewModel(get(), get()) }
}


