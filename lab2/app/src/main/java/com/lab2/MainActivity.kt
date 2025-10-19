package com.lab2

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lab2.ui.theme.Lab2Theme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab2Theme {
                TodoApp()
            }
        }
    }
}

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val done: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp() {
    val tasks = remember { mutableStateListOf<Task>() }

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    val canAdd = title.isNotBlank() && description.isNotBlank()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("My To-Do") })
        },
        floatingActionButton = {
            // Keep dependencies minimal: FAB with text instead of an icon
            FloatingActionButton(
                onClick = {
                    if (canAdd) {
                        tasks.add(Task(title = title.trim(), description = description.trim()))
                        title = ""; description = ""
                    }
                }
            ) { Text("+") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Input card
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (canAdd) {
                                tasks.add(Task(title = title.trim(), description = description.trim()))
                                title = ""; description = ""
                            }
                        },
                        enabled = canAdd,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add task")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tasks yet. Add your first one!",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = tasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggleDone = { id ->
                                val idx = tasks.indexOfFirst { it.id == id }
                                if (idx != -1) tasks[idx] = tasks[idx].copy(done = !tasks[idx].done)
                            },
                            onDelete = { id ->
                                tasks.removeAll { it.id == id }
                            }
                        )
                    }
                    item { Spacer(Modifier.height(72.dp)) } // space above FAB
                }
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: Task,
    onToggleDone: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.done)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.done,
                onCheckedChange = { onToggleDone(task.id) }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = task.title,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            TextButton(onClick = { onDelete(task.id) }) {
                Text("Delete")
            }
        }
    }
}


@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun TodoAppPreviewDark() {
    Lab2Theme { TodoApp() }
}
