package com.demo.repositoriesviewer.domain

class GetRepositoriesUseCase(private val appRepository: AppRepository) {

    fun getRepositories(): List<Repo> {
        return appRepository.getRepositories()
    }
}