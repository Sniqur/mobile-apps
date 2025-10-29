package com.lab4.data.db

import androidx.room.TypeConverter
import com.lab4.data.model.LabStatus
import com.lab4.data.model.SubjectStatus

class Converters {
    @TypeConverter
    fun fromSubjectStatus(value: SubjectStatus?): String? = value?.name

    @TypeConverter
    fun toSubjectStatus(value: String?): SubjectStatus =
        value?.let { runCatching { SubjectStatus.valueOf(it) }.getOrNull() }
            ?: SubjectStatus.NOT_STARTED

    @TypeConverter
    fun fromLabStatus(value: LabStatus?): String? = value?.name

    @TypeConverter
    fun toLabStatus(value: String?): LabStatus =
        value?.let { runCatching { LabStatus.valueOf(it) }.getOrNull() }
            ?: LabStatus.NOT_STARTED
}
