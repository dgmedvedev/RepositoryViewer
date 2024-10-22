package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.data.storage.KeyValueStorage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val navController: NavController by lazy {
        findNavController(R.id.container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        val keyValueStorage = KeyValueStorage(this)
        val token = keyValueStorage.authToken

        if (token.isNullOrBlank()) {
            launchFragment(R.id.authFragment)
        } else {
            launchFragment(R.id.repositoriesListFragment)
        }
    }

    private fun launchFragment(fragmentId: Int) {
        navController.navigateUp()
        navController.navigate(fragmentId)
    }
}