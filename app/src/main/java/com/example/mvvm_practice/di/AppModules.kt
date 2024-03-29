package com.example.mvvm_practice.di

import com.example.mvvm_practice.MainViewModel
import com.example.mvvm_practice.data.Repository
import com.example.mvvm_practice.data.StoragePreferencesRepository
import com.example.mvvm_practice.data.cursor.LocalUserCursorDatabase
import com.example.mvvm_practice.data.room.LocalUserDatabase
import com.example.mvvm_practice.ui.about.AboutViewModel
import com.example.mvvm_practice.ui.game.GameViewModel
import com.example.mvvm_practice.ui.storage.StorageViewModel
import com.example.mvvm_practice.ui.storage.about.AboutStorageViewModel
import com.example.mvvm_practice.ui.storage.add.AddLocalUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { CoroutineScope(SupervisorJob()) }

    // Room Database
    single { LocalUserDatabase.getDatabase(get(), get()) }
    // Cursor Database
    single { LocalUserCursorDatabase.getDatabase(get(), get()) }
    // Preferences for Storage
    single { StoragePreferencesRepository.getInstance(get()) }
    // Repository
    single {
        Repository(
            get<LocalUserDatabase>().localUserDao(),
            get<LocalUserCursorDatabase>(),
            get()
        )
    }

    viewModel { MainViewModel(get()) }
    viewModel { GameViewModel() }
    viewModel { StorageViewModel(get()) }
    viewModel { AddLocalUserViewModel(get()) }
    viewModel { AboutViewModel(get()) }
    viewModel { AboutStorageViewModel(get()) }
}