package com.demo.repositoriesviewer.data.mapper

import com.demo.repositoriesviewer.data.network.models.OwnerDto
import com.demo.repositoriesviewer.data.network.models.RepoDetailsDto
import com.demo.repositoriesviewer.data.network.models.RepoDto
import com.demo.repositoriesviewer.domain.models.License
import com.demo.repositoriesviewer.domain.models.Repo
import com.demo.repositoriesviewer.domain.models.RepoDetails
import com.demo.repositoriesviewer.domain.models.UserInfo

object RepoMapper {

    fun mapListReposDtoToDomain(listReposDto: List<RepoDto>): List<Repo> {
        return listReposDto.map { it.toDomain() }
    }

    fun ownerDtoToDomain(ownerDto: OwnerDto) = UserInfo(
        name = ownerDto.login
    )

    fun mapRepoDetailsDtoToDomain(
        repoDetailsDto: RepoDetailsDto,
        watchers: Int
    ) = RepoDetails(
        name = repoDetailsDto.repoName,
        url = repoDetailsDto.url,
        license = licenseDtoToDomain(repoDetailsDto),
        stars = repoDetailsDto.stargazersCount,
        forks = repoDetailsDto.forksCount,
        branchName = repoDetailsDto.branchName,
        userInfo = ownerDtoToDomain(ownerDto = repoDetailsDto.owner),
        watchers = watchers
    )

    private fun licenseDtoToDomain(repoDetailsDto: RepoDetailsDto) = License(
        spdxId = repoDetailsDto.license?.spdxId
    )

    private fun RepoDto.toDomain() = Repo(
        id = id,
        name = name,
        language = language,
        description = description
    )
}