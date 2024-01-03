package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.databinding.FragmentAuthBinding
import kotlinx.coroutines.launch

class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding: FragmentAuthBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentAuthBinding == null")

    private lateinit var authViewModel: AuthViewModel

    private var toastMessage: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
    }

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
        binding.etAuthorization.setText(token)

        observeViewModel()
        setListeners()

//        lifecycleScope.launch {
//            repository.signIn(token)
//            val listRepos = repository.getRepositories()
//
//            for ((i, repo) in listRepos.withIndex()) {
//                Log.d("TEST_RETROFIT", "repo${i + 1}: $repo")
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            authViewModel.actions.collect {
                handleAction(it)
            }
        }

        authViewModel.state.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            binding.buttonSignIn.isEnabled = true
            binding.buttonSignIn.setTextColor(resources.getColor(R.color.white))
            when (it) {
                is AuthViewModel.State.Loading -> {
                    binding.buttonSignIn.setTextColor(resources.getColor(R.color.button_auth))
                    binding.buttonSignIn.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                is AuthViewModel.State.InvalidInput -> {
                    binding.tilAuthorization.error = it.reason
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

    private fun setListeners() {
        with(binding) {
            buttonSignIn.setOnClickListener {
                val newToken: String = etAuthorization.text.toString()
                authViewModel.saveToken(newToken)
                authViewModel.onSignButtonPressed()
            }

            etAuthorization.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    tilAuthorization.error = null
                }
            })
        }
    }

    private fun handleAction(action: AuthViewModel.Action) {
        when (action) {
            AuthViewModel.Action.RouteToMain -> showToast(action.javaClass.simpleName)
            is AuthViewModel.Action.ShowError -> showToast(action.message)
        }
    }

    private fun showToast(message: String) {
        if (toastMessage != null) {
            toastMessage?.cancel()
        }
        toastMessage = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toastMessage?.show()
    }
}