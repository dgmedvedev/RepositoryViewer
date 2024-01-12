package com.demo.repositoriesviewer.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.demo.repositoriesviewer.databinding.FragmentRepositoriesListBinding
import com.demo.repositoriesviewer.presentation.adapter.RepoListAdapter

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
        repositoriesListViewModel = ViewModelProvider(this)[RepositoriesListViewModel::class.java]
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
        repositoriesListViewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        repositoriesListViewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RepositoriesListViewModel.State.Loading -> {
                    Log.d("TEST_TOKEN", "State.Loading")
                }
                is RepositoriesListViewModel.State.Loaded -> {
                    Log.d("TEST_TOKEN", "State.Loaded")
                    repoListAdapter.submitList(state.repos)
                }
                is RepositoriesListViewModel.State.Error -> {
                    Log.d("TEST_TOKEN", "State.Error")
                    Log.d("TEST_TOKEN", state.error)
                }
                is RepositoriesListViewModel.State.Empty -> {
                    Log.d("TEST_TOKEN", "State.Empty")
                }
            }
        }
    }

    private fun setListeners() {
        repoListAdapter.onRepoClickListener = {
            showToast(it.id)
        }
    }

    private fun showToast(message: String) {
        if (toastMessage != null) {
            toastMessage?.cancel()
        }
        toastMessage = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toastMessage?.show()
    }

    companion object {
        fun getInstance(): RepositoriesListFragment {
            return RepositoriesListFragment()
        }
    }
}