package com.demo.repositoriesviewer.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.repositoriesviewer.databinding.ItemRepoBinding
import com.demo.repositoriesviewer.domain.entities.Repo

class RepoListAdapter :
    ListAdapter<Repo, RepoListAdapter.RepoListViewHolder>(RepoDiffCallback()) {

    var onRepoClickListener: ((Repo) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoListViewHolder {
        val binding = ItemRepoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepoListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoListViewHolder, position: Int) {
        val repoItem = getItem(position)
        val binding = holder.binding

        binding.apply {
            tvRepoName.text = repoItem.repoDetails.name
            tvLanguage.text = repoItem.repoDetails.language
            tvDescription.text = repoItem.repoDetails.description

            root.setOnClickListener {
                onRepoClickListener?.invoke(repoItem)
            }
        }
    }

    class RepoListViewHolder(val binding: ItemRepoBinding) :
        RecyclerView.ViewHolder(binding.root)
}