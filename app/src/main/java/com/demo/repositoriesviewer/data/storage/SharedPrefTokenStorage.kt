package com.demo.repositoriesviewer.data.storage

import android.content.Context
import com.demo.repositoriesviewer.data.storage.models.KeyValueStorage

private const val SHARED_PREFERENCE_NAME = "shared_preference"
private const val SHARED_PREFERENCE_KEY = "token_value"
private const val VALUE_IS_EMPTY = ""

class SharedPrefTokenStorage(context: Context) : TokenStorage {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    override fun save(keyValueStorage: KeyValueStorage) {
        sharedPreferences.edit().putString(SHARED_PREFERENCE_KEY, keyValueStorage.authToken).apply()
    }

    override fun get(): KeyValueStorage {
        val authToken = sharedPreferences.getString(SHARED_PREFERENCE_KEY, VALUE_IS_EMPTY)
        return KeyValueStorage(authToken = authToken)
    }
}