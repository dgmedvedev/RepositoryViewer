package com.demo.repositoriesviewer.domain.usecases

import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.repository.AppRepository

class GetRepositoriesUseCase(private val appRepository: AppRepository) {

    operator fun invoke(): List<Repo> {
        return appRepository.getRepositories()
    }
}