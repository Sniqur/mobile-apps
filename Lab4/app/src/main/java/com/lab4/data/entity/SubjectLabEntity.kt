package com.lab4.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lab4.data.model.LabStatus

/**
 * SubjectLabEntity - the data class which represents the `subjectsLabs` table
 * - replaces boolean flags with single status
 * - keeps optional comment
 */
@Entity(
    tableName = "subjectsLabs",
    indices = [Index(value = ["subject_id"])],
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subject_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubjectLabEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "subject_id") val subjectId: Int,
    val title: String,
    val description: String,
    val comment: String? = null,
    val status: LabStatus = LabStatus.NOT_STARTED,
)
