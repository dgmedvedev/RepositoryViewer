package com.demo.repositoriesviewer.presentation.adapter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.databinding.ItemRepoBinding
import com.demo.repositoriesviewer.domain.models.Repo

class RepoItemViewHolder(private val binding: ItemRepoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private lateinit var repository: Repo

    fun bind(repositoryItem: Repo, listener: RepoListAdapter.OnRepoClickListener) {
        this.repository = repositoryItem
        val language = this.repository.language
        val color = when (language) {
            JAVA, JAVA_SCRIPT -> ContextCompat.getColor(
                itemView.context,
                R.color.yellow
            )

            SWIFT -> ContextCompat.getColor(itemView.context, R.color.green)
            else -> ContextCompat.getColor(itemView.context, R.color.purple)
        }

        binding.tvRepoName.text = this.repository.name
        binding.tvDescription.text = this.repository.description
        binding.tvLanguage.text = language
        binding.tvLanguage.setTextColor(color)

        itemView.setOnClickListener {
            listener.onRepoClick(this.repository)
        }
    }

    companion object {
        private const val JAVA = "Java"
        private const val JAVA_SCRIPT = "JavaScript"
        private const val SWIFT = "Swift"
    }
}