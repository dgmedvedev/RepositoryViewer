package com.demo.repositoriesviewer.di

import android.content.Context
import com.demo.repositoriesviewer.data.repository.AppRepositoryImpl
import com.demo.repositoriesviewer.data.storage.KeyValueStorage
import com.demo.repositoriesviewer.domain.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideKeyValueStorage(@ApplicationContext applicationContext: Context): KeyValueStorage {
        return KeyValueStorage(context = applicationContext)
    }

    @Provides
    @Singleton
    fun provideAppRepository(keyValueStorage: KeyValueStorage): AppRepository {
        return AppRepositoryImpl(keyValueStorage = keyValueStorage)
    }
}