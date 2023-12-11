package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.demo.repositoriesviewer.R

class MainActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    private var toastMessage: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val etAuth = findViewById<EditText>(R.id.et_authorization)
        val button = findViewById<Button>(R.id.bt_sign_in)

        val token = authViewModel.getToken()
        Log.d("TEST_TOKEN", "token: $token")
        etAuth.setText(token)

        observeViewModel()

        button.setOnClickListener {
            val newToken: String = etAuth.text.toString()
            authViewModel.saveToken(newToken)
            authViewModel.onSignButtonPressed()
        }

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
            //authViewModel.saveToken(it)
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