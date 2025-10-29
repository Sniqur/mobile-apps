package com.lab4.ui.screens.subjectDetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lab4.data.db.DatabaseStorage
import com.lab4.data.entity.SubjectEntity
import com.lab4.data.entity.SubjectLabEntity
import com.lab4.data.model.LabStatus
import com.lab4.data.model.SubjectStatus
import com.lab4.ui.navigation.SubjectDetailsRoute
import com.lab4.ui.theme.Lab4Theme
import kotlinx.coroutines.launch

@Composable
fun SubjectDetailsScreen(
    route: SubjectDetailsRoute,
) {
    val context = LocalContext.current
    val db = DatabaseStorage.getDatabase(context)
    val scope = rememberCoroutineScope()

    val subjectState = remember { mutableStateOf<SubjectEntity?>(null) }
    val subjectLabsState = remember { mutableStateOf<List<SubjectLabEntity>>(emptyList()) }

    // Локальні стани для редагування
    val subjectStatus = remember { mutableStateOf(SubjectStatus.NOT_STARTED) }
    val subjectComment = remember { mutableStateOf("") }

    LaunchedEffect(route.id) {
        val subj = db.subjectsDao.getSubjectById(route.id)
        subjectState.value = subj
        subjectStatus.value = subj?.status ?: SubjectStatus.NOT_STARTED
        subjectComment.value = subj?.comment ?: ""

        subjectLabsState.value = db.subjectLabsDao.getSubjectLabsBySubjectId(route.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Дисципліна", fontSize = 28.sp, fontWeight = FontWeight.SemiBold)

        // Блок дисципліни
        SubjectEditorCard(
            status = subjectStatus.value,
            onStatusChange = { subjectStatus.value = it },
            comment = subjectComment.value,
            onCommentChange = { subjectComment.value = it },
            onSave = {
                val id = subjectState.value?.id ?: return@SubjectEditorCard
                scope.launch {
                    db.subjectsDao.updateSubjectStatus(id, subjectStatus.value)
                    db.subjectsDao.updateSubjectComment(id, subjectComment.value.ifBlank { null })
                }
            },
            title = buildString {
                append("ID: ${subjectState.value?.id ?: "-"}  ")
                append("Назва: ${subjectState.value?.title.orEmpty()}")
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
                items = subjectLabsState.value,
                key = { it.id ?: it.title.hashCode() }
            ) { lab ->
                // Локальний стан по кожній лабі
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
                        scope.launch {
                            db.subjectLabsDao.updateLabStatus(labId, labStatus)
                            db.subjectLabsDao.updateLabComment(
                                labId,
                                labComment.ifBlank { null }
                            )
                        }
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

            // Dropdown статусу
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

/**
 * Preview: без доступу до БД
 */
@Preview(showBackground = true)
@Composable
private fun SubjectDetailsScreenPreview() {
    Lab4Theme {
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
