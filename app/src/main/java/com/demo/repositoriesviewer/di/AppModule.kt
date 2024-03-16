package com.demo.repositoriesviewer.di

import android.content.Context
import com.demo.repositoriesviewer.presentation.adapter.RepoListAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRepoListAdapter(@ApplicationContext context: Context): RepoListAdapter {
        return RepoListAdapter(context = context)
    }
}