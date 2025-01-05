package com.demo.repositoriesviewer.app

import android.app.Application
import com.demo.repositoriesviewer.data.storage.KeyValueStorage
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        KeyValueStorage.initialize(this)
    }
}