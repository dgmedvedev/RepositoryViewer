package com.demo.repositoriesviewer.domain.usecases

import com.demo.repositoriesviewer.domain.entities.UserInfo
import com.demo.repositoriesviewer.domain.repository.AppRepository

class SignInUseCase(private val appRepository: AppRepository) {

    suspend operator fun invoke(token: String): UserInfo {
        return appRepository.signIn(token)
    }
}