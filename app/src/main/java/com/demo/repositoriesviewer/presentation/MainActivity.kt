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

        val authorizationHeader = String.format(
            getString(R.string.authorization_header),
            "TODO() - token"
        )
        val user = "dgmedvedev"

        val apiService = ApiFactory.apiService

        lifecycleScope.launch {
            val listRepositories = apiService.getListRepos(authorizationHeader)
            val fullListRepositories = apiService.getFullListRepos(user)

            for ((i, repo) in fullListRepositories.withIndex()) {
                Log.d("TEST_RETROFIT", "repo${i + 1}: $repo")
            }
            for ((i, repo) in listRepositories.withIndex()) {
                Log.d("TEST_RETROFIT", "repo${i + 1}: $repo")
            }
        }
    }
}