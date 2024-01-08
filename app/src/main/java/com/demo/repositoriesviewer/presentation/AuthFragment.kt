package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleAction(action: AuthViewModel.Action) {
        when (action) {
            AuthViewModel.Action.RouteToMain -> routeSuccess()
            is AuthViewModel.Action.ShowError -> showError(action.message)
        }
    }

    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            authViewModel.actions.collect { action ->
                handleAction(action)
            }
        }

        authViewModel.state.observe(viewLifecycleOwner) { state ->
            val color = if (state is AuthViewModel.State.Loading) {
                ContextCompat.getColor(requireContext(), R.color.button_auth)
            } else {
                ContextCompat.getColor(requireContext(), R.color.white)
            }
            binding.signButton.setTextColor(color)
            binding.signButton.isEnabled = state != AuthViewModel.State.Loading

            binding.progressBar.visibility =
                if (state == AuthViewModel.State.Loading) View.VISIBLE else View.GONE

            binding.tilAuthorization.error = if (state is AuthViewModel.State.InvalidInput) {
                state.reason
            } else {
                null
            }
        }

        authViewModel.token.observe(viewLifecycleOwner) {
            //TODO()
        }
    }

    private fun routeSuccess() {
        launchFragment(RepositoriesListFragment.getInstance())
    }

    private fun setListeners() {
        with(binding) {
            signButton.setOnClickListener {
                val newToken: String = etAuthorization.text.toString()
                authViewModel.repository.keyValueStorage.authToken = newToken
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

    private fun showError(error: String) {
        val message = when (error) {
            HTTP_401_ERROR -> getString(R.string.requires_authentication_error)
            HTTP_403_ERROR -> getString(R.string.forbidden_error)
            HTTP_404_ERROR -> getString(R.string.resource_not_found_error)
            HTTP_422_ERROR -> getString(R.string.validation_failed_error)
            else -> getString(R.string.unknown_error)
        }
        showToast("$message ($error)")
    }

    private fun showToast(message: String) {
        if (toastMessage != null) {
            toastMessage?.cancel()
        }
        toastMessage = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toastMessage?.show()
    }

    companion object {
        const val HTTP_401_ERROR = "HTTP 401 "
        const val HTTP_403_ERROR = "HTTP 403 "
        const val HTTP_404_ERROR = "HTTP 404 "
        const val HTTP_422_ERROR = "HTTP 422 "
    }
}