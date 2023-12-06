package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.data.AppRepositoryImpl
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token = ""

        val repository = AppRepositoryImpl(this)

        lifecycleScope.launch {
            repository.signIn(token)
            val listRepos = repository.getRepositories()

            for ((i, repo) in listRepos.withIndex()) {
                Log.d("TEST_RETROFIT", "repo${i + 1}: $repo")
            }
        }
    }
}