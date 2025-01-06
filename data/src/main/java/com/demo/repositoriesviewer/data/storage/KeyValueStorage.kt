package com.demo.repositoriesviewer.data.storage

import android.content.Context

class KeyValueStorage(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    var authToken: String?
        get() = sharedPreferences.getString(SHARED_PREFERENCE_KEY, VALUE_IS_EMPTY)
        set(token) = sharedPreferences.edit().putString(SHARED_PREFERENCE_KEY, token).apply()

    fun clear() {
        authToken = null
    }

    companion object {
        private const val SHARED_PREFERENCE_NAME = "shared_preference"
        private const val SHARED_PREFERENCE_KEY = "token_value"
        private const val VALUE_IS_EMPTY = ""
    }
}