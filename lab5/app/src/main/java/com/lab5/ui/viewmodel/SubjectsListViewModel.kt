package com.lab5.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab5.data.dao.SubjectDao
import com.lab5.data.dao.SubjectLabsDao
import com.lab5.data.entity.SubjectEntity
import com.lab5.data.entity.SubjectLabEntity
import com.lab5.data.model.LabStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubjectProgress(
    val completed: Int,
    val total: Int
)

data class SubjectsListUiState(
    val subjects: List<SubjectEntity> = emptyList(),
    val labs: List<SubjectLabEntity> = emptyList(),
    val progressBySubject: Map<Int, SubjectProgress> = emptyMap()
)

class SubjectsListViewModel(
    private val subjectsDao: SubjectDao,
    private val subjectLabsDao: SubjectLabsDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectsListUiState())
    val uiState: StateFlow<SubjectsListUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val subjects = subjectsDao.getAllSubjects()
            val labs = subjectLabsDao.getAllSubjectLabs()

            val progressBySubject = labs.groupBy { it.subjectId }.mapValues { (_, labs) ->
                val total = labs.size.coerceAtLeast(1)
                val completed = labs.count { it.status == LabStatus.COMPLETED }
                SubjectProgress(completed, total)
            }

            _uiState.value = SubjectsListUiState(
                subjects = subjects,
                labs = labs,
                progressBySubject = progressBySubject
            )
        }
    }

    fun refresh() {
        loadData()
    }
}

