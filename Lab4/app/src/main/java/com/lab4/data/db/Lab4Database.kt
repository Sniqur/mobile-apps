package com.lab4.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lab4.data.dao.SubjectDao
import com.lab4.data.dao.SubjectLabsDao
import com.lab4.data.entity.SubjectEntity
import com.lab4.data.entity.SubjectLabEntity
import com.lab4.data.model.LabStatus
import com.lab4.data.model.SubjectStatus
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [SubjectEntity::class, SubjectLabEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class Lab4Database : RoomDatabase() {
    abstract val subjectsDao: SubjectDao
    abstract val subjectLabsDao: SubjectLabsDao
}

object DatabaseStorage {
    private val coroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
        },
    )

    private var _database: Lab4Database? = null

    fun getDatabase(context: Context): Lab4Database {
        _database?.let { return it }
        return Room.databaseBuilder(
            context,
            Lab4Database::class.java,
            "lab4Database"
        )
            .fallbackToDestructiveMigration()
            .build()
            .also {
                _database = it
                preloadData()
            }
    }

    private fun preloadData() {
        val db = _database ?: return

        val subjects = listOf(
            com.lab4.data.entity.SubjectEntity(
                title = "Операційні системи",
                status = SubjectStatus.IN_PROGRESS
            ),
            com.lab4.data.entity.SubjectEntity(
                title = "Комп’ютерні мережі",
                status = SubjectStatus.IN_PROGRESS
            ),
            com.lab4.data.entity.SubjectEntity(
                title = "Мобільна розробка",
                status = SubjectStatus.NOT_STARTED
            ),
            com.lab4.data.entity.SubjectEntity(
                title = "Бази даних",
                status = SubjectStatus.POSTPONED,
                comment = "Чекаю матеріали від викладача"
            ),
            com.lab4.data.entity.SubjectEntity(
                title = "Алгоритми та структури даних",
                status = SubjectStatus.COMPLETED
            ),
        )

        coroutineScope.launch {
            try {
                val subjectIds = db.subjectsDao.addSubjects(subjects)
                val idOs     = subjectIds.getOrNull(0)?.toInt() ?: 1
                val idNet    = subjectIds.getOrNull(1)?.toInt() ?: 2
                val idMobile = subjectIds.getOrNull(2)?.toInt() ?: 3
                val idDb     = subjectIds.getOrNull(3)?.toInt() ?: 4
                val idAlgo   = subjectIds.getOrNull(4)?.toInt() ?: 5

                val labs = listOf(
                    com.lab4.data.entity.SubjectLabEntity(
                        subjectId = idOs,
                        title = "Лаб1: Процеси та потоки",
                        description = "Створення та синхронізація потоків",
                        status = LabStatus.COMPLETED,
                        comment = "Перевірено викладачем"
                    ),
                    com.lab4.data.entity.SubjectLabEntity(
                        subjectId = idOs,
                        title = "Лаб2: Планування",
                        description = "Алгоритми планування CPU",
                        status = LabStatus.IN_PROGRESS
                    ),
                    com.lab4.data.entity.SubjectLabEntity(
                        subjectId = idNet,
                        title = "Лаб1: Сабнеттінг",
                        description = "IPv4/IPv6, маски, підмережі",
                        status = LabStatus.IN_PROGRESS
                    ),
                    com.lab4.data.entity.SubjectLabEntity(
                        subjectId = idNet,
                        title = "Лаб2: Маршрутизація",
                        description = "Статика/динаміка, таблиці",
                        status = LabStatus.NOT_STARTED,
                        comment = "Почати після Лаб1"
                    ),
                    com.lab4.data.entity.SubjectLabEntity(
                        subjectId = idMobile,
                        title = "Лаб1: Compose basics",
                        description = "Списки, стани, навігація",
                        status = LabStatus.NOT_STARTED
                    ),
                    com.lab4.data.entity.SubjectLabEntity(
                        subjectId = idDb,
                        title = "Лаб1: SQL основи",
                        description = "SELECT/INSERT/UPDATE/DELETE",
                        status = LabStatus.POSTPONED,
                        comment = "Потрібна консультація"
                    ),
                    com.lab4.data.entity.SubjectLabEntity(
                        subjectId = idDb,
                        title = "Лаб2: ORM/Room",
                        description = "Entities, DAO, Converters, Relations",
                        status = LabStatus.NOT_STARTED
                    ),
                    com.lab4.data.entity.SubjectLabEntity(
                        subjectId = idAlgo,
                        title = "Лаб1: Сортування",
                        description = "Quick/Merge/Heap sort",
                        status = LabStatus.COMPLETED
                    ),
                    com.lab4.data.entity.SubjectLabEntity(
                        subjectId = idAlgo,
                        title = "Лаб2: Графи",
                        description = "BFS/DFS, shortest paths",
                        status = LabStatus.COMPLETED
                    ),
                )

                db.subjectLabsDao.addSubjectLabs(labs)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }
}
