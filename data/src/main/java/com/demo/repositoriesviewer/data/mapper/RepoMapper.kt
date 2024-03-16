package com.demo.repositoriesviewer.data.mapper

import com.demo.repositoriesviewer.data.network.models.OwnerDto
import com.demo.repositoriesviewer.data.network.models.RepoDto
import com.demo.repositoriesviewer.data.storage.models.KeyValueStorage
import com.demo.repositoriesviewer.domain.entities.*

class RepoMapper {

    fun keyValueToKeyValueStorage(keyValue: KeyValue): KeyValueStorage = KeyValueStorage(
        authToken = keyValue.authToken
    )

    fun keyValueStorageToKeyValue(keyValueStorage: KeyValueStorage): KeyValue = KeyValue().also {
        it.authToken = keyValueStorage.authToken
    }


    fun mapListReposDtoToListRepos(listReposDto: List<RepoDto>): List<Repo> {
        return listReposDto.map { repoDtoToRepo(repoDto = it) }
    }

    fun ownerDtoToUserInfo(ownerDto: OwnerDto) = UserInfo(
        name = ownerDto.login
    )

    private fun licenseDtoToLicense(repoDto: RepoDto) = License(
        spdxId = repoDto.license?.spdxId
    )

    private fun repoDetailsDtoToRepoDetails(repoDto: RepoDto) = RepoDetails(
        forks = repoDto.forksCount,
        stars = repoDto.stargazersCount,
        branchName = repoDto.branchName,
        description = repoDto.description,
        language = repoDto.language,
        name = repoDto.name,
        url = repoDto.url,
        license = licenseDtoToLicense(repoDto),
        userInfo = ownerDtoToUserInfo(repoDto.owner)
    )

    private fun repoDtoToRepo(repoDto: RepoDto) = Repo(
        id = repoDto.id,
        repoDetails = repoDetailsDtoToRepoDetails(repoDto)
    )
}