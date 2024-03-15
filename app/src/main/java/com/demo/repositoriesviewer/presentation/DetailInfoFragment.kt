package com.demo.repositoriesviewer.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.databinding.FragmentDetailInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailInfoFragment : Fragment() {

    private val args by navArgs<DetailInfoFragmentArgs>()

    private var _binding: FragmentDetailInfoBinding? = null
    private val binding: FragmentDetailInfoBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentDetailInfoBinding == null")

    private val repositoryInfoViewModel: RepositoryInfoViewModel by viewModels()

    private var toastMessage: Toast? = null

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

        repositoryInfoViewModel.loadData(args.repoId)
        observeViewModel()
        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        repositoryInfoViewModel.state.observe(viewLifecycleOwner) { state ->
            if (state is RepositoryInfoViewModel.State.Loaded) {
                binding.repositoryName.text = state.githubRepo.repoDetails.name
                binding.tvRepoUrl.text = state.githubRepo.repoDetails.url
                binding.tvLicense.text =
                    state.githubRepo.repoDetails.license?.spdxId
                        ?: getString(R.string.without_a_license)
                binding.tvStars.text = state.githubRepo.repoDetails.stars.toString()
                binding.tvForks.text = state.githubRepo.repoDetails.forks.toString()
                binding.tvWatchers.text = state.githubRepo.repoDetails.watchers.toString()
            }
            if (state is RepositoryInfoViewModel.State.Error) {
                showError(error = state.error)
            }
            if (state is RepositoryInfoViewModel.State.Loading) {
                viewIsInvisible()
            } else {
                viewIsVisible()
            }
        }
        repositoryInfoViewModel.readmeState.observe(viewLifecycleOwner) { readmeState ->
            binding.progressBar.visibility =
                if (readmeState == RepositoryInfoViewModel.ReadmeState.Loading) {
                    binding.tvReadme.visibility = View.GONE
                    View.VISIBLE
                } else {
                    binding.tvReadme.visibility = View.VISIBLE
                    View.GONE
                }
            binding.tvReadme.text =
                if (readmeState is RepositoryInfoViewModel.ReadmeState.Loaded) readmeState.markdown
                else getString(R.string.readme_empty)
            if (readmeState is RepositoryInfoViewModel.ReadmeState.Error) {
                showError(error = readmeState.error)
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
            openUrl(url = url)
        }
        binding.backStack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.signOut.setOnClickListener {
            findNavController().navigate(
                DetailInfoFragmentDirections.actionDetailInfoFragmentToAuthFragment()
            )
        }
    }

    private fun showError(error: String) {
        with(RepositoryInfoViewModel) {
            val message = when (error) {
                HTTP_401_ERROR -> getString(R.string.requires_authentication_error)
                HTTP_403_ERROR -> getString(R.string.forbidden_error)
                HTTP_404_ERROR -> getString(R.string.resource_not_found_error)
                HTTP_422_ERROR -> getString(R.string.validation_failed_error)
                OWNER_NAME_IS_NULL_OR_BLANK -> getString(R.string.owner_name_is_null_or_blank)
                else -> getString(R.string.unknown_error)
            }
            showToast(message = message)
        }
    }

    private fun showToast(message: String) {
        toastMessage?.cancel()
        toastMessage = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toastMessage?.show()
    }

    private fun viewIsInvisible() {
        with(binding) {
            tvRepoUrl.visibility = View.GONE
            ivLicenseLabel.visibility = View.GONE
            tvTitleLicense.visibility = View.GONE
            tvLicense.visibility = View.GONE
            ivLabelStars.visibility = View.GONE
            tvStars.visibility = View.GONE
            tvTitleStars.visibility = View.GONE
            ivLabelForks.visibility = View.GONE
            tvForks.visibility = View.GONE
            tvTitleForks.visibility = View.GONE
            ivLabelWatchers.visibility = View.GONE
            tvWatchers.visibility = View.GONE
            tvTitleWatchers.visibility = View.GONE
        }
    }

    private fun viewIsVisible() {
        with(binding) {
            tvRepoUrl.visibility = View.VISIBLE
            ivLicenseLabel.visibility = View.VISIBLE
            tvTitleLicense.visibility = View.VISIBLE
            tvLicense.visibility = View.VISIBLE
            ivLabelStars.visibility = View.VISIBLE
            tvStars.visibility = View.VISIBLE
            tvTitleStars.visibility = View.VISIBLE
            ivLabelForks.visibility = View.VISIBLE
            tvForks.visibility = View.VISIBLE
            tvTitleForks.visibility = View.VISIBLE
            ivLabelWatchers.visibility = View.VISIBLE
            tvWatchers.visibility = View.VISIBLE
            tvTitleWatchers.visibility = View.VISIBLE
        }
    }
}