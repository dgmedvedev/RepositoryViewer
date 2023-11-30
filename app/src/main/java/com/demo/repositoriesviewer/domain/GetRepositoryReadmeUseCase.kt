package com.demo.repositoriesviewer.domain

class GetRepositoryReadmeUseCase(private val appRepository: AppRepository) {

    fun getRepositoryReadme(ownerName: String, repositoryName: String, branchName: String): String {
        return appRepository.getRepositoryReadme(ownerName, repositoryName, branchName)
    }
}