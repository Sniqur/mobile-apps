package com.lab5.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab5.data.dao.SubjectDao
import com.lab5.data.dao.SubjectLabsDao
import com.lab5.data.entity.SubjectEntity
import com.lab5.data.entity.SubjectLabEntity
import com.lab5.data.model.LabStatus
import com.lab5.data.model.SubjectStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubjectDetailsUiState(
    val subject: SubjectEntity? = null,
    val subjectLabs: List<SubjectLabEntity> = emptyList(),
    val subjectStatus: SubjectStatus = SubjectStatus.NOT_STARTED,
    val subjectComment: String = ""
)

class SubjectDetailsViewModel(
    private val subjectId: Int,
    private val subjectsDao: SubjectDao,
    private val subjectLabsDao: SubjectLabsDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectDetailsUiState())
    val uiState: StateFlow<SubjectDetailsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val subject = subjectsDao.getSubjectById(subjectId)
            val subjectLabs = subjectLabsDao.getSubjectLabsBySubjectId(subjectId)

            _uiState.value = SubjectDetailsUiState(
                subject = subject,
                subjectLabs = subjectLabs,
                subjectStatus = subject?.status ?: SubjectStatus.NOT_STARTED,
                subjectComment = subject?.comment ?: ""
            )
        }
    }

    fun updateSubjectStatus(status: SubjectStatus) {
        viewModelScope.launch {
            subjectsDao.updateSubjectStatus(subjectId, status)
            _uiState.value = _uiState.value.copy(subjectStatus = status)
            loadData() // Reload to get updated subject
        }
    }

    fun updateSubjectComment(comment: String) {
        viewModelScope.launch {
            subjectsDao.updateSubjectComment(subjectId, comment.ifBlank { null })
            _uiState.value = _uiState.value.copy(subjectComment = comment)
            loadData() // Reload to get updated subject
        }
    }

    fun updateLabStatus(labId: Int, status: LabStatus) {
        viewModelScope.launch {
            subjectLabsDao.updateLabStatus(labId, status)
            loadData() // Reload to get updated labs
        }
    }

    fun updateLabComment(labId: Int, comment: String) {
        viewModelScope.launch {
            subjectLabsDao.updateLabComment(labId, comment.ifBlank { null })
            loadData() // Reload to get updated labs
        }
    }
}

