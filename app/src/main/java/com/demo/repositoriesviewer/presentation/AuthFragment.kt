package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.databinding.FragmentAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding: FragmentAuthBinding
        get() = _binding
            ?: throw java.lang.RuntimeException("FragmentAuthBinding == null")

    private val authViewModel: AuthViewModel by viewModels()

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
        observeViewModel()
        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchFragment() {
        findNavController().navigate(R.id.action_authFragment_to_repositoriesListFragment)
    }

    private fun observeViewModel() {
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
                if (state is AuthViewModel.State.InvalidInput) state.reason else null
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
                val token: String = etAuthorization.text.toString()
                authViewModel.onSignButtonPressed(token = token)
            }
            etAuthorization.addTextChangedListener {
                tilAuthorization.error = null
            }
        }
    }

    private fun showError(error: String) {
        with(AuthViewModel){
            val message = when (error) {
                HTTP_401_ERROR -> getString(R.string.requires_authentication_error)
                HTTP_403_ERROR -> getString(R.string.forbidden_error)
                HTTP_404_ERROR -> getString(R.string.resource_not_found_error)
                HTTP_422_ERROR -> getString(R.string.validation_failed_error)
                INTERNET_ACCESS_ERROR -> getString(R.string.internet_access_error)
                VALUE_INVALID -> getString(R.string.value_invalid)
                VALUE_NOT_ENTERED -> getString(R.string.value_not_entered)
                else -> String.format(getString(R.string.unknown_error), error)
            }
            showToast(message)
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