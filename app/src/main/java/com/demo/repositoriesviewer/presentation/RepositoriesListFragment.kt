package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.databinding.FragmentRepositoriesListBinding
import com.demo.repositoriesviewer.presentation.adapter.RepoListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class RepositoriesListFragment : Fragment() {

    @Inject
    lateinit var repoListAdapter: RepoListAdapter

    private var _binding: FragmentRepositoriesListBinding? = null
    private val binding: FragmentRepositoriesListBinding
        get() = _binding
            ?: throw java.lang.RuntimeException("FragmentRepositoriesListBinding == null")

    private val repositoriesListViewModel: RepositoriesListViewModel by viewModels()

    private var toastMessage: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepositoriesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.repoRecyclerView.adapter = repoListAdapter
        observeViewModel()
        setListeners()
        val deferredInternetAvailable = lifecycleScope.async {
            withContext(Dispatchers.IO) {
                repositoriesListViewModel.isInternetAvailable()
            }
        }
        lifecycleScope.launch {
            val isInternetAvailable = deferredInternetAvailable.await()
            if (isInternetAvailable) {
                repositoriesListViewModel.loadData()
            } else {
                showToast(message = getString(R.string.internet_access_error))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchFragment(repoId: String) {
        findNavController().navigate(
            RepositoriesListFragmentDirections.actionRepositoriesListFragmentToDetailInfoFragment(
                repoId = repoId
            )
        )
    }

    private fun observeViewModel() {
        repositoriesListViewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility =
                if (state == RepositoriesListViewModel.State.Loading) View.VISIBLE else View.GONE

            if (state is RepositoriesListViewModel.State.Loaded) {
                repoListAdapter.submitList(state.repos)
            }
            if (state is RepositoriesListViewModel.State.Error) {
                showError(error = state.error)
            }
            if (state == RepositoriesListViewModel.State.Empty) {
                showToast(message = getString(R.string.list_is_empty))
            }
        }
    }

    private fun setListeners() {
        repoListAdapter.onRepoClickListener = {
            val deferredInternetAvailable = lifecycleScope.async {
                withContext(Dispatchers.IO) {
                    repositoriesListViewModel.isInternetAvailable()
                }
            }
            lifecycleScope.launch {
                val isInternetAvailable = deferredInternetAvailable.await()
                if (isInternetAvailable) {
                    launchFragment(it.id)
                } else {
                    showToast(message = getString(R.string.internet_access_error))
                }
            }
        }
        binding.signOut.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showError(error: String) {
        with(RepositoriesListViewModel) {
            val message = when (error) {
                HTTP_401_ERROR -> getString(R.string.requires_authentication_error)
                HTTP_403_ERROR -> getString(R.string.forbidden_error)
                HTTP_404_ERROR -> getString(R.string.resource_not_found_error)
                HTTP_422_ERROR -> getString(R.string.validation_failed_error)
                else -> String.format(getString(R.string.unknown_error), error)
            }
            showToast(message = message)
        }
    }

    private fun showToast(message: String) {
        toastMessage?.cancel()
        toastMessage = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toastMessage?.show()
    }
}