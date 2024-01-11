package com.demo.repositoriesviewer.data.mapper

import com.demo.repositoriesviewer.data.network.model.OwnerDto
import com.demo.repositoriesviewer.data.network.model.RepoDto
import com.demo.repositoriesviewer.domain.entities.License
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.entities.UserInfo

class RepoMapper {

    fun mapListReposDtoToListRepos(listReposDto: List<RepoDto>): List<Repo> {
        return listReposDto.map { repoDtoToRepo(it) }
    }

    fun ownerDtoToUserInfo(ownerDto: OwnerDto) = UserInfo(
        name = ownerDto.login
    )

    private fun repoDtoToRepo(repoDto: RepoDto) = Repo(
        id = repoDto.id,
        repoDetails = repoDetailsDtoToRepoDetails(repoDto)
    )

    private fun repoDetailsDtoToRepoDetails(repoDto: RepoDto) = RepoDetails(
        forks = repoDto.forksCount,
        stars = repoDto.stargazersCount,
        watchers = repoDto.watchersCount,
        branchName = repoDto.branchName,
        description = repoDto.description,
        language = repoDto.language,
        name = repoDto.name,
        url = repoDto.url,
        license = licenseDtoToLicense(repoDto),
        userInfo = ownerDtoToUserInfo(repoDto.owner)
    )

    private fun licenseDtoToLicense(repoDto: RepoDto) = License(
        spdxId = repoDto.license?.spdxId
    )
}