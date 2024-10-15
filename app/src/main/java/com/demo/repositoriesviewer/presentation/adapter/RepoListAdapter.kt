package com.demo.repositoriesviewer.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.demo.repositoriesviewer.databinding.ItemRepoBinding
import com.demo.repositoriesviewer.domain.models.Repo

class RepoListAdapter(private val onRepoClickListener: OnRepoClickListener) :
    ListAdapter<Repo, RepoItemViewHolder>(RepoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoItemViewHolder {
        val binding = ItemRepoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepoItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(repositoryItem = item, listener = onRepoClickListener)
    }

    interface OnRepoClickListener {
        fun onRepoClick(repo: Repo)
    }
}