package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.databinding.FragmentRepositoriesListBinding
import com.demo.repositoriesviewer.presentation.adapter.RepoListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RepositoriesListFragment : Fragment() {

    private var _binding: FragmentRepositoriesListBinding? = null
    private val binding: FragmentRepositoriesListBinding
        get() = _binding
            ?: throw java.lang.RuntimeException("FragmentRepositoriesListBinding == null")

    private lateinit var repositoriesListViewModel: RepositoriesListViewModel

    private var toastMessage: Toast? = null

    private val repoListAdapter by lazy {
        RepoListAdapter(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repositoriesListViewModel =
            ViewModelProvider(this)[RepositoriesListViewModel::class.java]
    }

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
                showToast(getString(R.string.internet_access_error))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun observeViewModel() {
        repositoriesListViewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.visibility =
                if (state == RepositoriesListViewModel.State.Loading) View.VISIBLE else View.GONE

            if (state is RepositoriesListViewModel.State.Loaded) {
                repoListAdapter.submitList(state.repos)
            }
            if (state is RepositoriesListViewModel.State.Error) {
                showError(state.error)
            }
            if (state == RepositoriesListViewModel.State.Empty) {
                showToast(getString(R.string.list_is_empty))
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
                    launchFragment(DetailInfoFragment.getInstance(it.id))
                } else {
                    showToast(getString(R.string.internet_access_error))
                }
            }
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

        fun getInstance(): RepositoriesListFragment {
            return RepositoriesListFragment()
        }
    }
}