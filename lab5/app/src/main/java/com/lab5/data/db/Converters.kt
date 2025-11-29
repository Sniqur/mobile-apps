package com.lab5.data.db

import androidx.room.TypeConverter
import com.lab5.data.model.LabStatus
import com.lab5.data.model.SubjectStatus

class Converters {
    @TypeConverter
    // From enum to String
    fun fromSubjectStatus(value: SubjectStatus?): String? = value?.name

    @TypeConverter
    // From String to Enum
    fun toSubjectStatus(value: String?): SubjectStatus =
        value?.let { runCatching { SubjectStatus.valueOf(it) }.getOrNull() }
            ?: SubjectStatus.NOT_STARTED

    @TypeConverter
    // From enum to String
    fun fromLabStatus(value: LabStatus?): String? = value?.name

    @TypeConverter
    // From String to Enum
    fun toLabStatus(value: String?): LabStatus =
        value?.let { runCatching { LabStatus.valueOf(it) }.getOrNull() }
            ?: LabStatus.NOT_STARTED
}
