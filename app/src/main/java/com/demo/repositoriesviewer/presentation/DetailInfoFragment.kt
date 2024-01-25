package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.databinding.FragmentDetailInfoBinding

class DetailInfoFragment : Fragment() {

    private var _binding: FragmentDetailInfoBinding? = null
    private val binding: FragmentDetailInfoBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentDetailInfoBinding == null")

    private lateinit var repositoryInfoViewModel: RepositoryInfoViewModel

    private var repoId: String? = null
    private var toastMessage: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repositoryInfoViewModel = ViewModelProvider(this)[repositoryInfoViewModel::class.java]
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailInfoBinding.inflate(inflater, container, false)
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

    private fun parseParams() {
        repoId = requireArguments().getString(REPO_ID)
    }

    private fun observeViewModel() {
        repositoryInfoViewModel.state.observe(viewLifecycleOwner){

        }
        repositoryInfoViewModel.readmeState.observe(viewLifecycleOwner){

        }
    }

    private fun setListeners() {

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
        private const val HTTP_401_ERROR = "HTTP 401 "
        private const val HTTP_403_ERROR = "HTTP 403 "
        private const val HTTP_404_ERROR = "HTTP 404 "
        private const val HTTP_422_ERROR = "HTTP 422 "
        private const val REPO_ID = "REPO_ID"

        fun getInstance(repoId: String): DetailInfoFragment {
            return DetailInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(REPO_ID, repoId)
                }
            }
        }
    }
}