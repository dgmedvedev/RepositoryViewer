package com.demo.repositoriesviewer.data.storage

import android.content.Context

class KeyValueStorage private constructor(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    var authToken: String?
        get() = sharedPreferences.getString(SHARED_PREFERENCES_KEY, VALUE_IS_EMPTY)
        set(token) = sharedPreferences.edit().putString(SHARED_PREFERENCES_KEY, token).apply()

    companion object {
        private const val SHARED_PREFERENCES_NAME = "shared_preferences"
        private const val SHARED_PREFERENCES_KEY = "token_value"
        private const val VALUE_IS_EMPTY = ""

        @Volatile
        private var instance: KeyValueStorage? = null

        fun getInstance(context: Context): KeyValueStorage {
            instance?.let { return it }
            synchronized(KeyValueStorage::class.java) {
                instance?.let { return it }
                val keyValueStorage = KeyValueStorage(context)
                instance = keyValueStorage
                return keyValueStorage
            }
        }
    }
}