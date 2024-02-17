package com.demo.repositoriesviewer.data

import android.app.Application
import java.io.File

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        val dexOutputDir: File = codeCacheDir
        dexOutputDir.setReadOnly()
    }
}