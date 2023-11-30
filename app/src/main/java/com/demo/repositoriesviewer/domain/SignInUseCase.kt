package com.demo.repositoriesviewer.domain

class SignInUseCase(private val appRepository: AppRepository) {

    fun signIn(token: String): UserInfo {
        return appRepository.signIn(token)
    }
}