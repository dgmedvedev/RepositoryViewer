package com.demo.repositoriesviewer.domain

class GetRepositoryUseCase(private val appRepository: AppRepository) {

    fun getRepository(repoId: String): RepoDetails {
        return appRepository.getRepository(repoId)
    }
}