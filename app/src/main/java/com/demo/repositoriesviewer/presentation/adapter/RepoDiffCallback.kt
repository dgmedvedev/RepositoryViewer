package com.demo.repositoriesviewer.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.demo.repositoriesviewer.domain.models.Repo

class RepoDiffCallback : DiffUtil.ItemCallback<Repo>() {
    override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
        return oldItem == newItem
    }
}