package com.demo.repositoriesviewer.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.demo.repositoriesviewer.databinding.FragmentDetailInfoBinding
import com.demo.repositoriesviewer.R
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailInfoFragment : Fragment(R.layout.fragment_detail_info) {

    private val args by navArgs<DetailInfoFragmentArgs>()

    private val binding by viewBinding(FragmentDetailInfoBinding::bind)

    private val repositoryInfoViewModel: RepositoryInfoViewModel by viewModels()

    private var toastMessage: Toast? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repositoryInfoViewModel.loadData(args.repoId)
        bindViewModel()
        setListeners()
    }

    private fun bindViewModel() {
        repositoryInfoViewModel.state.observe(viewLifecycleOwner) { state ->
            viewIsVisible()
            if (state is RepositoryInfoViewModel.State.Loaded) {
                setLayoutParams(state)
            }
            if (state is RepositoryInfoViewModel.State.Error) {
                showError(error = state.error)
            }
            if (state is RepositoryInfoViewModel.State.Loading) {
                viewIsInvisible()
            }
        }
        repositoryInfoViewModel.readmeState.observe(viewLifecycleOwner) { readmeState ->
            binding.tvReadme.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.tvReadme.text =
                if (readmeState is RepositoryInfoViewModel.ReadmeState.Loaded) readmeState.markdown
                else getString(R.string.readme_empty)
            if (readmeState is RepositoryInfoViewModel.ReadmeState.Error) {
                showError(error = readmeState.error)
            }
            if (readmeState is RepositoryInfoViewModel.ReadmeState.Loading) {
                binding.tvReadme.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
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
            findNavController().popBackStack(R.id.authFragment, false)
        }
    }

    private fun showError(error: String) {
        val message = when (error) {
            getString(R.string.http_401_error) -> getString(R.string.requires_authentication_error)
            getString(R.string.http_403_error) -> getString(R.string.forbidden_error)
            getString(R.string.http_404_error) -> getString(R.string.resource_not_found_error)
            getString(R.string.http_422_error) -> getString(R.string.validation_failed_error)
            RepositoryInfoViewModel.OWNER_NAME_IS_NULL_OR_BLANK -> getString(R.string.owner_name_is_null_or_blank)
            else -> String.format(getString(R.string.unknown_error), error)
        }
        showToast(message = message)
    }

    private fun showToast(message: String) {
        toastMessage?.cancel()
        toastMessage = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toastMessage?.show()
    }

    private fun setLayoutParams(state: RepositoryInfoViewModel.State.Loaded) {
        with(binding) {
            repositoryName.text = state.githubRepo.name
            tvRepoUrl.text = state.githubRepo.url
            tvLicense.text =
                state.githubRepo.license?.spdxId
                    ?: getString(R.string.without_a_license)
            tvStars.text = state.githubRepo.stars.toString()
            tvForks.text = state.githubRepo.forks.toString()
            tvWatchers.text = state.githubRepo.watchers.toString()
        }
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