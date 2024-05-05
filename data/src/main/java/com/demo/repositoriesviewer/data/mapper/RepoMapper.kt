package com.demo.repositoriesviewer.data.mapper

import com.demo.repositoriesviewer.data.network.models.OwnerDto
import com.demo.repositoriesviewer.data.network.models.RepoDto
import com.demo.repositoriesviewer.data.network.models.RepoItemDto
import com.demo.repositoriesviewer.domain.models.License
import com.demo.repositoriesviewer.domain.models.Repo
import com.demo.repositoriesviewer.domain.models.RepoDetails
import com.demo.repositoriesviewer.domain.models.RepoItem
import com.demo.repositoriesviewer.domain.models.UserInfo

object RepoMapper {

    fun mapListReposDtoToListRepos(listReposDto: List<RepoDto>): List<Repo> {
        return listReposDto.map { it.toDomain() }
    }

    fun mapListRepoItemsFromDtoToDomain(listRepoItemsDto: List<RepoItemDto>): List<RepoItem> {
        return listRepoItemsDto.map { it.toDomain() }
    }

    fun ownerDtoToUserInfo(ownerDto: OwnerDto) = UserInfo(
        name = ownerDto.login
    )

    private fun licenseDtoToLicense(repoDto: RepoDto) = License(
        spdxId = repoDto.license?.spdxId
    )

    private fun RepoItemDto.toDomain() = RepoItem(
        id = id,
        name = name,
        language = language,
        description = description
    )

    private fun RepoDto.toDomain() = Repo(
        id = id,
        repoDetails = RepoDetails(
            forks = forksCount,
            stars = stargazersCount,
            branchName = branchName,
            description = description,
            language = language,
            name = name,
            url = url,
            license = licenseDtoToLicense(this),
            userInfo = ownerDtoToUserInfo(owner)
        )
    )
}