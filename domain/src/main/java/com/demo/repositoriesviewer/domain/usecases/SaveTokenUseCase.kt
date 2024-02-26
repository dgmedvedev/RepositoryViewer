package com.demo.repositoriesviewer.domain.usecases

import com.demo.repositoriesviewer.domain.entities.KeyValue
import com.demo.repositoriesviewer.domain.repository.AppRepository

class SaveTokenUseCase(private val appRepository: AppRepository) {

    operator fun invoke(keyValue: KeyValue) {
        appRepository.saveToken(keyValue)
    }
}