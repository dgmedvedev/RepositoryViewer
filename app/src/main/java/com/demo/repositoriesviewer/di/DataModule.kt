package com.demo.repositoriesviewer.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.demo.repositoriesviewer.data.AppRepositoryImpl
import com.demo.repositoriesviewer.data.KeyValueStorage
import com.demo.repositoriesviewer.domain.repository.AppRepository
import com.demo.repositoriesviewer.presentation.AuthViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    fun provideSharedPreferences(@ApplicationContext application: Application): SharedPreferences {
        return application.getSharedPreferences(
            AuthViewModel.NAME_SHARED_PREFERENCE,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideKeyValueStorage(): KeyValueStorage {
        return KeyValueStorage()
    }

    @Provides
    @Singleton
    fun provideAppRepository(keyValueStorage: KeyValueStorage): AppRepository {
        return AppRepositoryImpl(keyValueStorage = keyValueStorage)
    }
}