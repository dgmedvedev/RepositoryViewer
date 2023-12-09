package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.data.AppRepositoryImpl
import com.demo.repositoriesviewer.data.KeyValueStorage
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val repository by lazy {
        AppRepositoryImpl(this)
    }

    private val authViewModel by lazy {
        ViewModelProvider(this)[AuthViewModel::class.java]
    }

    private val token by lazy {
        KeyValueStorage.authToken
    }

    private var toastMessage: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observeViewModel()

        authViewModel.onSignButtonPressed()

//        lifecycleScope.launch {
//            repository.signIn(token)
//            val listRepos = repository.getRepositories()
//
//            for ((i, repo) in listRepos.withIndex()) {
//                Log.d("TEST_RETROFIT", "repo${i + 1}: $repo")
//            }
//        }
    }

    private fun observeViewModel() {
        authViewModel.state.observe(this) {
            when (it) {
                is AuthViewModel.State.InvalidInput -> {
                    showToast(it.reason)
                }
                is AuthViewModel.State.Loading -> {
                    showToast("Loading")
                }
                is AuthViewModel.State.Idle -> {
                    showToast("Idle")
                }
            }
        }
        authViewModel.token.observe(this) {
            authViewModel.
        }
    }

    private fun showToast(message: String) {
        if (toastMessage != null) {
            toastMessage?.cancel()
        }
        toastMessage = Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
        toastMessage?.show()
    }
}