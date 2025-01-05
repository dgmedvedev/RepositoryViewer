package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.data.storage.KeyValueStorage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        val keyValueStorage = KeyValueStorage.get()
        val token = keyValueStorage.authToken

        val navController = findNavController(R.id.container)

        token?.let {
            navController.navigateUp()
            navController.navigate(R.id.repositoriesListFragment)
        }
    }
}