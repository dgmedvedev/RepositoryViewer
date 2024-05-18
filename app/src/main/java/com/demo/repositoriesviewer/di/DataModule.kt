package com.demo.repositoriesviewer.di

import android.content.Context
import com.demo.repositoriesviewer.data.repository.AppRepositoryImpl
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
    fun provideAppRepository(@ApplicationContext context: Context): AppRepository {
        return AppRepositoryImpl(context = context)
    }
}