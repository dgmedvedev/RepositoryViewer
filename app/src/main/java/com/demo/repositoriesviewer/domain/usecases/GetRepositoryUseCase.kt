package com.demo.repositoriesviewer.domain.usecases

import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.repository.AppRepository

class GetRepositoryUseCase(private val appRepository: AppRepository) {

    operator fun invoke(repoId: String): RepoDetails {
        return appRepository.getRepository(repoId)
    }
}