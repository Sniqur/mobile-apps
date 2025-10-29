package com.example.myapplication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

/** Модель даних */
data class Destination(
    val id: Int,
    val name: String,
    val city: String,
    val description: String
)

/** Прості демо-дані */
val demoDestinations = listOf(
    Destination(1, "Києво-Печерська лавра", "Київ", "Печери, монастир, музеї, краєвиди на Дніпро."),
    Destination(2, "Площа Ринок", "Львів", "Історичний центр, ратуша, кав’ярні, кам’яниці."),
    Destination(3, "Софія Київська", "Київ", "UNESCO, мозаїки та фрески XI ст."),
    Destination(4, "Кам'янець-Подільська фортеця", "Кам'янець-Подільський", "Фортеця на скелях, панорамні види."),
    Destination(5, "Оперний театр", "Одеса", "Відомий театр з екскурсіями та виставами.")
)

/** Екран списку */
@Composable
fun DestinationsListScreen(
    items: List<Destination>,
    onItemClick: (Destination) -> Unit,
    modifier: Modifier = Modifier
) {
    // Легке "повітря" по краях
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentPadding = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            Card(
                shape = CardDefaults.shape, // округлення за темою
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) }
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        item.city,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/** Екран деталей */
@Composable
fun DestinationDetailScreen(
    item: Destination,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Титул
        Text(
            item.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            item.city,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        Divider()
        Spacer(Modifier.height(12.dp))

        // Опис
        Text(
            item.description,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(24.dp))

        // Кнопки
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад до списку")
        }
    }
}

/* ---- PREVIEWS ---- */
@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    MyApplicationTheme {
        DestinationsListScreen(demoDestinations, onItemClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    MyApplicationTheme {
        DestinationDetailScreen(demoDestinations.first(), onBack = {})
    }
}
