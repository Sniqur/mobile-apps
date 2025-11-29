package com.lab5.ui.screens.subjectDetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lab5.data.model.LabStatus
import com.lab5.data.model.SubjectStatus
import com.lab5.ui.navigation.SubjectDetailsRoute
import com.lab5.ui.theme.Lab5Theme
import com.lab5.ui.viewmodel.SubjectDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SubjectDetailsScreen(
    route: SubjectDetailsRoute,
    viewModel: SubjectDetailsViewModel = koinViewModel(parameters = { parametersOf(route.id) })
) {
    val uiState by viewModel.uiState.collectAsState()

    // Local states for modifying
    var subjectStatus by remember { mutableStateOf(uiState.subjectStatus) }
    var subjectComment by remember { mutableStateOf(uiState.subjectComment) }

    // Update local state when UI state changes
    LaunchedEffect(uiState.subjectStatus) {
        subjectStatus = uiState.subjectStatus
    }
    LaunchedEffect(uiState.subjectComment) {
        subjectComment = uiState.subjectComment
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Дисципліна", fontSize = 28.sp, fontWeight = FontWeight.SemiBold)

        // Subject Block
        SubjectEditorCard(
            status = subjectStatus,
            onStatusChange = { subjectStatus = it },
            comment = subjectComment,
            onCommentChange = { subjectComment = it },
            onSave = {
                viewModel.updateSubjectStatus(subjectStatus)
                viewModel.updateSubjectComment(subjectComment)
            },
            title = buildString {
                append("ID: ${uiState.subject?.id ?: "-"}  ")
                append("Назва: ${uiState.subject?.title.orEmpty()}")
            }
        )

        Spacer(Modifier.height(16.dp))
        Text(text = "Лабораторні", fontSize = 28.sp, fontWeight = FontWeight.SemiBold)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 8.dp)
        ) {
            items(
                items = uiState.subjectLabs,
                key = { it.id ?: it.title.hashCode() }
            ) { lab ->
                // Local state for every Lab work
                var labStatus by remember(lab.id) { mutableStateOf(lab.status) }
                var labComment by remember(lab.id) { mutableStateOf(lab.comment ?: "") }

                LabEditorCard(
                    title = "Лаба: ${lab.title}",
                    subtitle = "Опис: ${lab.description}",
                    status = labStatus,
                    onStatusChange = { labStatus = it },
                    comment = labComment,
                    onCommentChange = { labComment = it },
                    onSave = {
                        val labId = lab.id ?: return@LabEditorCard
                        viewModel.updateLabStatus(labId, labStatus)
                        viewModel.updateLabComment(labId, labComment)
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SubjectEditorCard(
    status: SubjectStatus,
    onStatusChange: (SubjectStatus) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    onSave: () -> Unit,
    title: String
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // Dropdown thingy
            SubjectStatusDropdown(status = status, onStatusChange = onStatusChange)

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                label = { Text("Коментар до дисципліни") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Button(onClick = onSave) {
                Text("Зберегти дисципліну")
            }
        }
    }
}

@Composable
private fun LabEditorCard(
    title: String,
    subtitle: String,
    status: LabStatus,
    onStatusChange: (LabStatus) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(8.dp))
            LabStatusDropdown(status = status, onStatusChange = onStatusChange)

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                label = { Text("Коментар до роботи") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Button(onClick = onSave) {
                Text("Зберегти роботу")
            }
        }
    }
}

@Composable
private fun SubjectStatusDropdown(
    status: SubjectStatus,
    onStatusChange: (SubjectStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(text = "Статус дисципліни:", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(6.dp))
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(status.name.replace('_', ' '))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                SubjectStatus.values().forEach { s ->
                    DropdownMenuItem(
                        text = { Text(s.name.replace('_', ' ')) },
                        onClick = {
                            onStatusChange(s)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LabStatusDropdown(
    status: LabStatus,
    onStatusChange: (LabStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(text = "Статус роботи:", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(6.dp))
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(status.name.replace('_', ' '))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                LabStatus.values().forEach { s ->
                    DropdownMenuItem(
                        text = { Text(s.name.replace('_', ' ')) },
                        onClick = {
                            onStatusChange(s)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SubjectDetailsScreenPreview() {
    Lab5Theme {
        SubjectEditorCard(
            status = SubjectStatus.IN_PROGRESS,
            onStatusChange = {},
            comment = "Планую закрити до кінця тижня",
            onCommentChange = {},
            onSave = {},
            title = "ID: 1  Назва: Мобільна розробка"
        )

        Spacer(Modifier.height(8.dp))

        LabEditorCard(
            title = "Лаба: Jetpack Compose basics",
            subtitle = "Опис: списки, стани, навігація",
            status = LabStatus.NOT_STARTED,
            onStatusChange = {},
            comment = "",
            onCommentChange = {},
            onSave = {}
        )
    }
}
