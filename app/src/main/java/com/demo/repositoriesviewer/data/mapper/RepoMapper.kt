package com.demo.repositoriesviewer.data.mapper

import com.demo.repositoriesviewer.data.network.model.RepoDto
import com.demo.repositoriesviewer.domain.entities.License
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.entities.UserInfo

class RepoMapper {

    fun mapListReposDtoToListRepos(listReposDto: List<RepoDto>): List<Repo> {
        return listReposDto.map { repoDtoToRepo(it) }
    }

    private fun repoDtoToRepo(repoDto: RepoDto) = Repo(
        id = repoDto.id,
        repoDetailsDtoToRepoDetails(repoDto)
    )

    private fun repoDetailsDtoToRepoDetails(repoDto: RepoDto) = RepoDetails(
        repoDto.forksCount,
        repoDto.stargazersCount,
        repoDto.watchersCount,
        repoDto.branchName,
        repoDto.name,
        repoDto.url,
        repoLicenseDtoToLicense(repoDto),
        repoOwnerDtoToUserInfo(repoDto)
    )

    private fun repoLicenseDtoToLicense(repoDto: RepoDto) = License(
        repoDto.license?.spdxId
    )

    private fun repoOwnerDtoToUserInfo(repoDto: RepoDto) = UserInfo(
        repoDto.owner.login
    )
}