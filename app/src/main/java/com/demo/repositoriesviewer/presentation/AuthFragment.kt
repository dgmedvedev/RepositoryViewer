package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding: FragmentAuthBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentAuthBinding == null")

    private lateinit var authViewModel: AuthViewModel

    private var toastMessage: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val token = authViewModel.getToken()
        Log.d("TEST_TOKEN", "token: $token")
        binding.etAuthorization.setText(token)

        observeViewModel()

        binding.btSignIn.setOnClickListener {
            val newToken: String = binding.etAuthorization.text.toString()
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
        authViewModel.state.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            binding.btSignIn.isEnabled = true
            binding.btSignIn.setTextColor(resources.getColor(R.color.white))
            when (it) {
                is AuthViewModel.State.Loading -> {
                    binding.btSignIn.setTextColor(resources.getColor(R.color.button_auth))
                    binding.btSignIn.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                is AuthViewModel.State.InvalidInput -> {
                    showToast(it.reason)
                }
                is AuthViewModel.State.Idle -> {
                    showToast("Idle")
                }
            }
        }
        authViewModel.token.observe(viewLifecycleOwner) {
            //authViewModel.saveToken(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
    }

    private fun showToast(message: String) {
        if (toastMessage != null) {
            toastMessage?.cancel()
        }
        toastMessage = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toastMessage?.show()
    }
}