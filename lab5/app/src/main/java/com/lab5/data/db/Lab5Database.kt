package com.lab5.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lab5.data.dao.SubjectDao
import com.lab5.data.dao.SubjectLabsDao
import com.lab5.data.entity.SubjectEntity
import com.lab5.data.entity.SubjectLabEntity
import com.lab5.data.model.LabStatus
import com.lab5.data.model.SubjectStatus
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
abstract class Lab5Database : RoomDatabase() {
    abstract val subjectsDao: SubjectDao
    abstract val subjectLabsDao: SubjectLabsDao
}

object DatabaseStorage {
    private val coroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
        },
    )

    private var _database: Lab5Database? = null

    fun getDatabase(context: Context): Lab5Database {
        _database?.let { return it }
        return Room.databaseBuilder(
            context,
            Lab5Database::class.java,
            "lab5Database"
        )
            .fallbackToDestructiveMigration()
            .build()
            .also {
                _database = it
                preloadData()
            }
    }

    // Add initial data only if subjects table is empty.

    private fun preloadData() {
        val db = _database ?: return

        // Subject & Labs itself
        val subjects = listOf(
            SubjectEntity(
                title = "Розгортання ІКС",
                status = SubjectStatus.IN_PROGRESS
            ),
            SubjectEntity(
                title = "Проектування ІКС",
                status = SubjectStatus.NOT_STARTED
            ),
            SubjectEntity(
                title = "Програмування Мобільних додатків",
                status = SubjectStatus.IN_PROGRESS
            ),
            SubjectEntity(
                title = "Мережева Безпека",
                status = SubjectStatus.POSTPONED,
                comment = "Чекаю матеріали від викладача"
            ),
            SubjectEntity(
                title = "Економіка та підприємництво",
                status = SubjectStatus.COMPLETED
            ),
        )

        coroutineScope.launch {
            try {
                // If there is at least one subject - skip initial add
                val count = db.subjectsDao.getSubjectsCount()
                if (count > 0) return@launch

                val subjectIds = db.subjectsDao.addSubjects(subjects)
                val idOs   = subjectIds.getOrNull(0)?.toInt() ?: 1   // Розгортання ІКС
                val idNet  = subjectIds.getOrNull(1)?.toInt() ?: 2   // Проектування ІКС
                val idMob  = subjectIds.getOrNull(2)?.toInt() ?: 3   // Програмування Мобільних додатків
                val idSec  = subjectIds.getOrNull(3)?.toInt() ?: 4   // Мережева Безпека
                val idBiz  = subjectIds.getOrNull(4)?.toInt() ?: 5   // Економіка та підприємництво

                // 🔹 Names of Labs
                val labs = listOf(
                    // Розгортання ІКС
                    SubjectLabEntity(
                        subjectId = idOs,
                        title = "Докер: Вступ",
                        description = "Hello World Container",
                        status = LabStatus.COMPLETED,
                        comment = "Перевірено викладачем"
                    ),
                    SubjectLabEntity(
                        subjectId = idOs,
                        title = "Докер Компоуз",
                        description = "Створюємо мультиконтейнерний додаток",
                        status = LabStatus.IN_PROGRESS
                    ),

                    // Проектування ІКС
                    SubjectLabEntity(
                        subjectId = idNet,
                        title = "Лаб1: Сабнеттінг",
                        description = "IPv4/IPv6, маски, підмережі",
                        status = LabStatus.IN_PROGRESS
                    ),
                    SubjectLabEntity(
                        subjectId = idNet,
                        title = "Лаб2: Маршрутизація",
                        description = "Статика/динаміка, таблиці",
                        status = LabStatus.NOT_STARTED,
                        comment = "Почати після Лаб1"
                    ),

                    // Програмування Мобільних додатків
                    SubjectLabEntity(
                        subjectId = idMob,
                        title = "Kotlin: Вступ",
                        description = "Списки, стани, навігація",
                        status = LabStatus.NOT_STARTED
                    ),

                    // Мережева Безпека
                    SubjectLabEntity(
                        subjectId = idSec,
                        title = "Encryption: Вступ",
                        description = "MFA,2FA",
                        status = LabStatus.POSTPONED,
                        comment = "Потрібна консультація"
                    ),
                    SubjectLabEntity(
                        subjectId = idSec,
                        title = "Security: WI-FI Tower",
                        description = "Як не втрапити в пастку зловмисника",
                        status = LabStatus.NOT_STARTED
                    ),

                    // Економіка та підприємництво
                    SubjectLabEntity(
                        subjectId = idBiz,
                        title = "Data Science",
                        description = "Як рахувати прибуток",
                        status = LabStatus.COMPLETED
                    ),
                    SubjectLabEntity(
                        subjectId = idBiz,
                        title = "Лаб2: Графи",
                        description = "Як будувати інформативні графи",
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
