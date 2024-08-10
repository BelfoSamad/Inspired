package com.samadtch.inspired.data.repositories.impl

import com.samadtch.inspired.common.Result
import com.samadtch.inspired.common.asResult
import com.samadtch.inspired.common.di.Dispatcher
import com.samadtch.inspired.data.datasources.remote.FoldersRemoteDataSource
import com.samadtch.inspired.data.datasources.remote.dto.asExternalModel
import com.samadtch.inspired.data.repositories.FoldersRepository
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.Folder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FoldersRepositoryImpl(
    private val foldersRemoteDataSource: FoldersRemoteDataSource,
    private val dispatcher: Dispatcher
) : FoldersRepository {

    private val TEST_TOKEN = "eyJraWQiOiIyMzY4ZjRhYi00N2ZiLTQwN2MtYjM5Ni00NzgxODcwMjZkN2UiLCJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJwMktiX1NxWFpGUjhQSGFuWjZpa3NBIiwiY2xpZW50X2lkIjoiT0MtQVpFaDJsTWhvNTlfIiwiYXVkIjoiaHR0cHM6Ly93d3cuY2FudmEuY29tIiwiaWF0IjoxNzIzMzE2MjA2LCJuYmYiOjE3MjMzMTYyMDYsImV4cCI6MTcyMzMzMDYwNiwiYnVuZGxlcyI6W10sInRpZXIiOiJmcmVlIiwicm9sZXMiOiI0X1k4MGw3bllzN2c4d1ZnczZoenhlMnlsRU02LXBHSGJlWmNfX3lYazNVdnllUHo2ZWlfblp6U2NaTEFBWW13MjFrRVVGb1RKSEJQb0pia2FnQWp3ZjFSakF5YzNXQ1hzaG5fVS1QMEIzS1RKZ21CT3ptR0tKdnBZTjFZQlJOVmNSWVdISXZFU2lTNWJxNGdLNWxLLVQ4ZXZWWSIsImxvY2FsZSI6IkdRaU94ZjljMXI0YU1MWlRoTVBiMU5ITno1LU5KazMxbEZUTGJkcGNNeEFySWhpckFQVGJUYmxKQVJWY1dnNTVadlg4ckEiLCJzY29wZXMiOlsiZm9sZGVyOnJlYWQiLCJmb2xkZXI6d3JpdGUiLCJhc3NldDpyZWFkIiwiYXNzZXQ6d3JpdGUiLCJwcm9maWxlOnJlYWQiLCJjb21tZW50OnJlYWQiLCJjb21tZW50OndyaXRlIl0sInN1YiI6Im9VVlVOa0hMZGFJSVZRZVZxV0l1bzAiLCJicmFuZCI6Im9CVlVONEhZeHo1THZndG40X3ZDR3MiLCJvcmdhbml6YXRpb24iOm51bGwsImNvZGVfaWQiOiJUUEdFSFVsT253eElnMXlfbWFKSlhnIn0.JVMvjiX6DG9kQwKOJu204ITdtik-udOdk3Pf5ixu1g_NANjhZfqqBera3b-o6Ee4NM9fNRYcGbS6PGdcXhOg928ZK448fl3ghpi5eF9iOxkmG3HwAKa1UzoYcMpey_OHC30ZHq0Fl-JBLw_pkbTJQVCTYctmh9QOHwFoCgiAl_8z5UvNGx_VgbIlcjlJ7CW6noB1befF45iCPK_7KsroOY07d3P1nFheyf2xHnYHjsL_LvrpGqvLjY_i82BJOs3F8YpOSyT0LlnqnFeHXJtcucxLrzIofJYsj0L7euxI1TJRJKFhtwIkuTgkDELlINO4Eh2ebAMd_BSU7KZruNQJog"

    private suspend fun getPairedItems(
        token: String,
        folderId: String
    ): Pair<List<Folder>, List<Asset>> {
        val folders = mutableListOf<Folder>()
        val assets = mutableListOf<Asset>()

        val items = foldersRemoteDataSource.getFolderItems(token, folderId)

        for (item in items) {
            val childItems = if (item.folder?.id != null) getPairedItems(token, item.folder.id)
            else null
            when (item.type) {
                "folder" -> {
                    val folder = item.folder!!.asExternalModel()
                    folders.add(folder.copy(children = childItems?.first))
                }

                "asset" -> {
                    val asset = item.asset!!.asExternalModel()
                    assets.add(asset)
                    if (childItems != null) assets.addAll(childItems.second)
                }
            }
        }

        return folders to assets
    }

    override fun getAllItems(): Flow<Result<Pair<List<Folder>, List<Asset>>>> = flow {
        //TODO: Get Token
        emit(getPairedItems(TEST_TOKEN, "root"))
    }.flowOn(dispatcher.io).asResult()
}