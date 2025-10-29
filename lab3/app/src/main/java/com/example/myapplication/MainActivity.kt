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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.padding
// Два "екрани"
sealed class Screen {
    object List : Screen()
    data class Detail(val item: Destination) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyApplicationTheme { TourismApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourismApp() {
    var screen: Screen by remember { mutableStateOf(Screen.List) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (screen) {
                            is Screen.List -> "Популярні місця України"
                            is Screen.Detail -> "Деталі місця"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    ) { innerPadding ->
        when (val s = screen) {
            is Screen.List -> DestinationsListScreen(
                items = demoDestinations,
                onItemClick = { clicked -> screen = Screen.Detail(clicked) },
                modifier = Modifier.padding(innerPadding)
            )
            is Screen.Detail -> DestinationDetailScreen(
                item = s.item,
                onBack = { screen = Screen.List },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

/** Preview головного UI */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TourismAppPreview() {
    MyApplicationTheme { TourismApp() }
}
