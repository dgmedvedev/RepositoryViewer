package com.demo.repositoriesviewer.data.storage

import com.demo.repositoriesviewer.data.storage.models.KeyValueStorage

interface TokenStorage {

    fun save(keyValueStorage: KeyValueStorage)

    fun get(): KeyValueStorage
}