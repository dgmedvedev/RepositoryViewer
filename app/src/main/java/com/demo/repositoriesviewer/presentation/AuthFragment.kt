package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.demo.repositoriesviewer.databinding.FragmentAuthBinding
import kotlinx.coroutines.launch
import com.demo.repositoriesviewer.R
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val binding by viewBinding(FragmentAuthBinding::bind)

    private val authViewModel: AuthViewModel by viewModels()

    private var toastMessage: Toast? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        setListeners()
    }

    private fun launchFragment() {
        findNavController().navigate(R.id.action_authFragment_to_repositoriesListFragment)
    }

    private fun bindViewModel() {
        authViewModel.token.observe(viewLifecycleOwner) { token ->
            binding.etAuthorization.setText(token)
        }

        authViewModel.state.observe(viewLifecycleOwner) { state ->
            val color = if (state is AuthViewModel.State.Loading) {
                ContextCompat.getColor(requireContext(), R.color.green_button_auth)
            } else {
                ContextCompat.getColor(requireContext(), R.color.white)
            }
            binding.signButton.setTextColor(color)
            binding.signButton.isEnabled = state != AuthViewModel.State.Loading
            binding.progressBar.visibility =
                if (state is AuthViewModel.State.Loading) View.VISIBLE else View.GONE
            binding.tilAuthorization.error =
                if (state is AuthViewModel.State.InvalidInput) {
                    when (state.reason) {
                        R.string.value_invalid -> getString(R.string.value_invalid)
                        R.string.unexpected_char -> getString(R.string.unexpected_char)
                        R.string.value_not_entered -> getString(R.string.value_not_entered)
                        else -> String.format(
                            getString(R.string.unknown_error),
                            state.reason.toString()
                        )
                    }
                } else null
        }

        lifecycleScope.launch {
            authViewModel.actions.collect { action ->
                when (action) {
                    is AuthViewModel.Action.RouteToMain -> launchFragment()
                    is AuthViewModel.Action.ShowError -> showError(error = action.message)
                }
            }
        }
    }

    private fun setListeners() {
        with(binding) {
            signButton.setOnClickListener {
                val deferredInternetAvailable = lifecycleScope.async {
                    InternetCheck.isInternetAvailable()
                }
                lifecycleScope.launch {
                    val isInternetAvailable = deferredInternetAvailable.await()
                    if (isInternetAvailable) {
                        val token: String = etAuthorization.text.toString()
                        authViewModel.onSignButtonPressed(token = token)
                    } else {
                        showToast(message = getString(R.string.internet_access_error))
                    }
                }
            }
            etAuthorization.addTextChangedListener {
                tilAuthorization.error = null
            }
        }
    }

    private fun showError(error: String) {
        val message = when (error) {
            getString(R.string.http_401_error) -> getString(R.string.requires_authentication_error)
            getString(R.string.http_403_error) -> getString(R.string.forbidden_error)
            getString(R.string.http_404_error) -> getString(R.string.resource_not_found_error)
            getString(R.string.http_422_error) -> getString(R.string.validation_failed_error)
            else -> String.format(getString(R.string.unknown_error), error)
        }
        showToast(message = message)
    }

    private fun showToast(message: String) {
        toastMessage?.cancel()
        toastMessage = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toastMessage?.show()
    }
}