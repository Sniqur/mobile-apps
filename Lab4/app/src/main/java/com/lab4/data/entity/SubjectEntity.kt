package com.lab4.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lab4.data.model.SubjectStatus

//  SubjectEntity - the data class which represents the `subjects` table

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val title: String,
    val status: SubjectStatus = SubjectStatus.NOT_STARTED,
    val comment: String? = null,
)
