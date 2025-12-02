package com.example.lab7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab7.ui.screens.GameScreen
import com.example.lab7.ui.screens.WelcomeScreen
import com.example.lab7.ui.theme.Lab7Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab7Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TriviaApp()
                }
            }
        }
    }
}

@Composable
fun TriviaApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                onTopicSelected = { topic ->
                    val encodedTopic = java.net.URLEncoder.encode(topic, "UTF-8")
                    navController.navigate("game/$encodedTopic")
                }
            )
        }
        composable("game/{topic}") { backStackEntry ->
            val encodedTopic = backStackEntry.arguments?.getString("topic") ?: ""
            val topic = java.net.URLDecoder.decode(encodedTopic, "UTF-8")
            GameScreen(
                topic = topic,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}