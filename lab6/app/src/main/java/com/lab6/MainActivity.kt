package com.lab6

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.lab6.di.appModule
import com.lab6.ui.screen.WeatherScreen
import com.lab6.ui.theme.Lab6Theme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Lab6Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Lab6Application)
            modules(appModule)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab6Theme {
                WeatherScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

