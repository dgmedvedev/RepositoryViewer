package com.demo.repositoriesviewer.domain.usecases

import com.demo.repositoriesviewer.domain.entities.KeyValue
import com.demo.repositoriesviewer.domain.repository.AppRepository

class SaveKeyValueStorageUseCase(private val appRepository: AppRepository) {

    operator fun invoke(keyValue: KeyValue) {
        appRepository.saveKeyValue(keyValue)
    }
}