package com.demo.repositoriesviewer.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.databinding.ItemRepoBinding
import com.demo.repositoriesviewer.domain.models.RepoItem

class RepoListAdapter(private val context: Context) :
    ListAdapter<RepoItem, RepoListAdapter.RepoListViewHolder>(RepoDiffCallback()) {

    var onRepoClickListener: ((RepoItem) -> Unit)? = null

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
        val language = repoItem.language
        val color = when (language) {
            JAVA, JAVA_SCRIPT -> ContextCompat.getColor(context, R.color.yellow)
            SWIFT -> ContextCompat.getColor(context, R.color.green)
            else -> ContextCompat.getColor(context, R.color.purple)
        }

        binding.apply {
            tvRepoName.text = repoItem.name
            tvDescription.text = repoItem.description
            tvLanguage.text = language
            tvLanguage.setTextColor(color)

            root.setOnClickListener {
                onRepoClickListener?.invoke(repoItem)
            }
        }
    }

    companion object {
        private const val JAVA = "Java"
        private const val JAVA_SCRIPT = "JavaScript"
        private const val SWIFT = "Swift"
    }

    class RepoListViewHolder(val binding: ItemRepoBinding) :
        RecyclerView.ViewHolder(binding.root)
}