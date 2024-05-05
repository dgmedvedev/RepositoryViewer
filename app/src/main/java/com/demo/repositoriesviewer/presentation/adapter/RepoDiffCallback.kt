package com.demo.repositoriesviewer.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.demo.repositoriesviewer.domain.models.RepoItem

class RepoDiffCallback : DiffUtil.ItemCallback<RepoItem>() {
    override fun areItemsTheSame(oldItem: RepoItem, newItem: RepoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RepoItem, newItem: RepoItem): Boolean {
        return oldItem == newItem
    }
}