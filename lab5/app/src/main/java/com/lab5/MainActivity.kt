package com.lab5

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.lab5.di.appModule
import com.lab5.ui.navigation.NavigationGraph
import com.lab5.ui.theme.Lab5Theme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Lab5Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Lab5Application)
            modules(appModule)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab5Theme {
                NavigationGraph(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
