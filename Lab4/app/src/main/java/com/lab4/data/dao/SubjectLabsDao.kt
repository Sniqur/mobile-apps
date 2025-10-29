package com.lab4.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lab4.data.entity.SubjectLabEntity
import com.lab4.data.model.LabStatus

/**
 * SubjectLabsDao - interface of communication with `subjectsLabs` table
 */
@Dao
interface SubjectLabsDao {
    // fetch all labs
    @Query("SELECT * FROM subjectsLabs")
    suspend fun getAllSubjectLabs(): List<SubjectLabEntity>

    // fetch labs filtered by subjectId
    @Query("SELECT * FROM subjectsLabs WHERE subject_id = :subjectId")
    suspend fun getSubjectLabsBySubjectId(subjectId: Int): List<SubjectLabEntity>

    // insert/replace single lab (return generated id)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubjectLab(subjectLabEntity: SubjectLabEntity): Long

    // bulk insert (return generated ids)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubjectLabs(labs: List<SubjectLabEntity>): List<Long>

    // update whole entity
    @Update
    suspend fun updateSubjectLab(lab: SubjectLabEntity)

    // point updates
    @Query("UPDATE subjectsLabs SET status = :status WHERE id = :id")
    suspend fun updateLabStatus(id: Int, status: LabStatus)

    @Query("UPDATE subjectsLabs SET comment = :comment WHERE id = :id")
    suspend fun updateLabComment(id: Int, comment: String?)
}
