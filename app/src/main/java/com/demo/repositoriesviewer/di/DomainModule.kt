package com.demo.repositoriesviewer.di

import com.demo.repositoriesviewer.domain.repository.AppRepository
import com.demo.repositoriesviewer.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    fun provideGetRepositoriesUseCase(appRepository: AppRepository): GetRepositoriesUseCase =
        GetRepositoriesUseCase(appRepository = appRepository)

    @Provides
    fun provideGetRepositoryReadmeUseCase(appRepository: AppRepository): GetRepositoryReadmeUseCase =
        GetRepositoryReadmeUseCase(appRepository = appRepository)

    @Provides
    fun provideGetRepositoryUseCase(appRepository: AppRepository): GetRepositoryUseCase =
        GetRepositoryUseCase(appRepository = appRepository)

    @Provides
    fun provideGetTokenUseCase(appRepository: AppRepository): GetTokenUseCase =
        GetTokenUseCase(appRepository = appRepository)

    @Provides
    fun provideSaveTokenUseCase(appRepository: AppRepository): SaveTokenUseCase =
        SaveTokenUseCase(appRepository = appRepository)

    @Provides
    fun provideSignInUseCase(appRepository: AppRepository): SignInUseCase =
        SignInUseCase(appRepository = appRepository)
}