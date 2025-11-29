package com.lab5.di

import android.content.Context
import com.lab5.data.dao.SubjectDao
import com.lab5.data.dao.SubjectLabsDao
import com.lab5.data.db.Lab5Database
import com.lab5.data.db.DatabaseStorage
import com.lab5.ui.viewmodel.SubjectDetailsViewModel
import com.lab5.ui.viewmodel.SubjectsListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single<Lab5Database> {
        DatabaseStorage.getDatabase(androidContext())
    }

    // DAOs
    single<SubjectDao> {
        get<Lab5Database>().subjectsDao
    }

    single<SubjectLabsDao> {
        get<Lab5Database>().subjectLabsDao
    }

    // ViewModels
    viewModel { SubjectsListViewModel(get(), get()) }

    viewModel { params ->
        val subjectId: Int = params.get()
        SubjectDetailsViewModel(subjectId, get(), get())
    }
}

