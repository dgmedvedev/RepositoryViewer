package com.demo.repositoriesviewer.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
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
        repositoryInfoViewModel = ViewModelProvider(this)[RepositoryInfoViewModel::class.java]
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
        repoId?.let {
            repositoryInfoViewModel.loadData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseParams() {
        repoId = requireArguments().getString(REPO_ID)
    }

    private fun observeViewModel() {
        repositoryInfoViewModel.state.observe(viewLifecycleOwner) { state ->
            if (state is RepositoryInfoViewModel.State.Loaded) {
                binding.tvRepoUrl.text = state.githubRepo.repoDetails.url
                binding.tvLicense.text =
                    state.githubRepo.repoDetails.license?.spdxId
                        ?: getString(R.string.without_a_license)
                binding.tvStars.text = state.githubRepo.repoDetails.stars.toString()
                binding.tvForks.text = state.githubRepo.repoDetails.forks.toString()
                binding.tvWatchers.text = state.githubRepo.repoDetails.watchers.toString()
            }
            if (state is RepositoryInfoViewModel.State.Error) {
                showError(state.error)
            }
        }
        repositoryInfoViewModel.readmeState.observe(viewLifecycleOwner) { readmeState ->
            if (readmeState is RepositoryInfoViewModel.ReadmeState.Loaded) {
                binding.tvReadme.text =
                    HtmlCompat.fromHtml(readmeState.markdown, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
            if (readmeState is RepositoryInfoViewModel.ReadmeState.Empty) {
                binding.tvReadme.text = getString(R.string.readme_empty)
            }
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun setListeners() {
        binding.tvRepoUrl.setOnClickListener {
            val url = binding.tvRepoUrl.text.toString()
            openUrl(url)
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