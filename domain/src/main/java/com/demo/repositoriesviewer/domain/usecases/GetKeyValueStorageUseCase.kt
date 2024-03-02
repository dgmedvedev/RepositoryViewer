package com.demo.repositoriesviewer.domain.usecases

import com.demo.repositoriesviewer.domain.entities.KeyValue
import com.demo.repositoriesviewer.domain.repository.AppRepository

class GetKeyValueStorageUseCase(private val appRepository: AppRepository) {

    operator fun invoke(): KeyValue {
        return appRepository.getKeyValue()
    }
}