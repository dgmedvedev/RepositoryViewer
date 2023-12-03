package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.data.network.ApiFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiService = ApiFactory.apiService
        lifecycleScope.launch {
            val repositoriesList = apiService.getFullReposList()
            Log.d("TEST_RETROFIT", "$repositoriesList")
        }
    }
}