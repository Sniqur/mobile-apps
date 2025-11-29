package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyApplicationTheme { TourismApp() } }
    }
}

// Navigation routes
object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{destinationId}"
    
    fun detail(destinationId: Int) = "detail/$destinationId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourismApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (navController.currentDestination?.route) {
                            Routes.LIST -> "Популярні місця України"
                            else -> "Деталі місця"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LIST,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LIST) {
                DestinationsListScreen(
                    items = demoDestinations,
                    navController = navController
                )
            }
            composable(Routes.DETAIL) { backStackEntry ->
                val destinationId = backStackEntry.arguments?.getString("destinationId")?.toIntOrNull()
                val destination = demoDestinations.find { it.id == destinationId }
                
                if (destination != null) {
                    DestinationDetailScreen(
                        item = destination,
                        navController = navController
                    )
                }
            }
        }
    }
}

/** Preview Of main UI */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TourismAppPreview() {
    MyApplicationTheme { TourismApp() }
}
