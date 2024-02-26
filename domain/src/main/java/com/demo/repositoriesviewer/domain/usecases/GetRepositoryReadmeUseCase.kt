package com.demo.repositoriesviewer.domain.usecases

import com.demo.repositoriesviewer.domain.repository.AppRepository

class GetRepositoryReadmeUseCase(private val appRepository: AppRepository) {

    suspend operator fun invoke(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String {
        return appRepository.getRepositoryReadme(ownerName, repositoryName, branchName)
    }
}