package com.demo.repositoriesviewer.di

import android.content.Context
import com.demo.repositoriesviewer.data.AppRepositoryImpl
import com.demo.repositoriesviewer.data.mapper.RepoMapper
import com.demo.repositoriesviewer.data.network.ApiFactory
import com.demo.repositoriesviewer.data.network.ApiService
import com.demo.repositoriesviewer.data.storage.SharedPrefTokenStorage
import com.demo.repositoriesviewer.data.storage.TokenStorage
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
    fun provideApiService(): ApiService {
        return ApiFactory.apiService
    }

    @Provides
    @Singleton
    fun provideMapper(): RepoMapper {
        return RepoMapper()
    }

    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage {
        return SharedPrefTokenStorage(context = context)
    }

    @Provides
    @Singleton
    fun provideAppRepository(
        tokenStorage: TokenStorage,
        apiService: ApiService,
        mapper: RepoMapper
    ): AppRepository {
        return AppRepositoryImpl(
            tokenStorage = tokenStorage,
            apiService = apiService,
            mapper = mapper
        )
    }
}