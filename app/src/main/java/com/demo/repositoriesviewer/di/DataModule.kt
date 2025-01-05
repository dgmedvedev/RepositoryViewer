package com.demo.repositoriesviewer.di

import com.demo.repositoriesviewer.data.repository.AppRepositoryImpl
import com.demo.repositoriesviewer.domain.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideAppRepository(): AppRepository {
        return AppRepositoryImpl
    }
}