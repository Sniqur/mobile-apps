package com.lab4.ui.screens.subjectsList

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.lab4.ui.theme.Lab4Theme
import kotlin.math.roundToInt

@Composable
fun SubjectsListScreen(
    onDetailsScreen: (Int) -> Unit,
) {
    val context = LocalContext.current
    val db = DatabaseStorage.getDatabase(context)

    val subjectsState = remember { mutableStateOf<List<SubjectEntity>>(emptyList()) }
    val labsState = remember { mutableStateOf<List<SubjectLabEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        subjectsState.value = db.subjectsDao.getAllSubjects()
        labsState.value = db.subjectLabsDao.getAllSubjectLabs()
    }

    // The map of progress: subjectId -> Pair(completed, total)
    val progressBySubject = remember(subjectsState.value, labsState.value) {
        labsState.value.groupBy { it.subjectId }.mapValues { (_, labs) ->
            val total = labs.size.coerceAtLeast(1) // щоб не ділити на нуль
            val completed = labs.count { it.status == LabStatus.COMPLETED }
            completed to total
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(
                items = subjectsState.value,
                key = { it.id ?: it.title.hashCode() }
            ) { subject ->
                SubjectCardRow(
                    subject = subject,
                    completed = progressBySubject[subject.id ?: -1]?.first ?: 0,
                    total = progressBySubject[subject.id ?: -1]?.second ?: 0,
                    onClick = { subject.id?.let(onDetailsScreen) }
                )
            }
        }
    }
}

@Composable
private fun SubjectCardRow(
    subject: SubjectEntity,
    completed: Int,
    total: Int,
    onClick: () -> Unit
) {
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val progress = if (total == 0) 0f else completed.toFloat() / total.toFloat()

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current,
                onClick = onClick
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Name
            Text(
                text = subject.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(Modifier.height(6.dp))

            // Status
            Text(
                text = "Статус: ${subject.status.name.replace('_', ' ')}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Comment (Optional)
            subject.comment?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Коментар: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(10.dp))

            // Progress
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress =  progress ,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "${(progress * 100).roundToInt()}% ($completed/$total)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun SubjectsListScreenPreview() {
    Lab4Theme {
        Column(Modifier.padding(12.dp)) {
            SubjectCardRow(
                subject = SubjectEntity(id = 1, title = "Мобільна розробка"),
                completed = 1,
                total = 3
            ) {}
            SubjectCardRow(
                subject = SubjectEntity(id = 2, title = "Комп’ютерні мережі"),
                completed = 2,
                total = 2
            ) {}
        }
    }
}
