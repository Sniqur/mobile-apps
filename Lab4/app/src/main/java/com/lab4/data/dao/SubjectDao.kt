package com.lab4.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lab4.data.entity.SubjectEntity
import com.lab4.data.model.SubjectStatus


//  SubjectDao - interface of communication with `subjects` table

@Dao
interface SubjectDao {
    // fetch all subjects
    @Query("SELECT * FROM subjects")
    suspend fun getAllSubjects(): List<SubjectEntity>

    // fetch single Subject by id
    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Int): SubjectEntity?

    // count (для ідемпотентного сидування)
    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun getSubjectsCount(): Int

    // insert/replace Subject (return generated id)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubject(subjectEntity: SubjectEntity): Long

    // bulk insert (return generated ids)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSubjects(subjects: List<SubjectEntity>): List<Long>

    // update whole entity
    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    // point updates
    @Query("UPDATE subjects SET status = :status WHERE id = :id")
    suspend fun updateSubjectStatus(id: Int, status: SubjectStatus)

    @Query("UPDATE subjects SET comment = :comment WHERE id = :id")
    suspend fun updateSubjectComment(id: Int, comment: String?)
}
