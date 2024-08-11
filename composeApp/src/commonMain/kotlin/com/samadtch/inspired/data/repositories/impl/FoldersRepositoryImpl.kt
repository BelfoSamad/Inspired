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
import kotlinx.coroutines.withContext

class FoldersRepositoryImpl(
    private val foldersRemoteDataSource: FoldersRemoteDataSource,
    private val dispatcher: Dispatcher
) : FoldersRepository {

    //TODO: Get Proper Token
    private val TEST_TOKEN = "eyJraWQiOiIyMzY4ZjRhYi00N2ZiLTQwN2MtYjM5Ni00NzgxODcwMjZkN2UiLCJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJSMEpLQXg5OHVkYVIyXzJySkx6YlZ3IiwiY2xpZW50X2lkIjoiT0MtQVpFaDJsTWhvNTlfIiwiYXVkIjoiaHR0cHM6Ly93d3cuY2FudmEuY29tIiwiaWF0IjoxNzIzMzcwMzM2LCJuYmYiOjE3MjMzNzAzMzYsImV4cCI6MTcyMzM4NDczNiwiYnVuZGxlcyI6W10sInRpZXIiOiJmcmVlIiwicm9sZXMiOiJzeFVLSlp0Mmc3ckhYY182REdiVVV2b2FmeURjWDQxM1VxVXE0alkzQ2V5dk1YWGZTanBLT3hyQV9HcDI0WkxRWnZRWjZrMDFRMDE1ZzFqUEpleUJ1ZFRiUkFJTGhLOFpvM2tvZzFZUlVpNHg5VWRuR1Z6RUc4V0lNcHJyc2VIS2wxME04S2lVRHRKeUxGa1ZlTVY5SWFvbUpGNCIsImxvY2FsZSI6ImNvTlExa1U1cWV2R3BGdUV0cUxlcHFianYxTlpLTlhDM2VVWGtjRVU2UEdSOFdHQktRVm9rQUdoTDhMeUdza3ZJcVNnSkEiLCJzY29wZXMiOlsiZm9sZGVyOnJlYWQiLCJmb2xkZXI6d3JpdGUiLCJhc3NldDpyZWFkIiwiYXNzZXQ6d3JpdGUiLCJwcm9maWxlOnJlYWQiLCJjb21tZW50OnJlYWQiLCJjb21tZW50OndyaXRlIl0sInN1YiI6Im9VVlVOa0hMZGFJSVZRZVZxV0l1bzAiLCJicmFuZCI6Im9CVlVONEhZeHo1THZndG40X3ZDR3MiLCJvcmdhbml6YXRpb24iOm51bGwsImNvZGVfaWQiOiJUUEdFSFVsT253eElnMXlfbWFKSlhnIn0.GV9HfmGhE5iJiVOH6hsga4Qv9sQIoSoRI2vQmBHwb59AYYQqwfqUpx8qvIpE2hk4gf6jen3WqSjGQjiCKiuk8kSQa4GoL2zb6uhh3jQGATgnPyXUPVGNtLjylVndh07HYUgCq8rtCK-GX3nGPOCe_C55aj5sgqJGui2JVjXNwx50E-iYgbzkLi9x9ZkB_AM8KDRX1jLfDYpSehVjpPku79xBs7fs1E6Y8IyWeUR8LumGWhAH3Y4Tkf11VwLWcoclHwDuLsESjq3jc-lIN1H9zk3W9f_wMmCsIrrs1YVFfr0vmyPl2VpBQuQrhv8Xn27OulEi1ZR68ytktdlG6-7E8w"

    private suspend fun getPairedItems(
        token: String,
        folderId: String
    ): Pair<List<Folder>, List<Asset>> {
        println("Folder Checked: $folderId")
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
                    if(childItems != null) assets.addAll(childItems.second)
                }

                "asset" -> {
                    val asset = item.asset!!.asExternalModel()
                    assets.add(asset)
                }
            }
        }
        return folders to assets
    }

    override fun getAllItems(): Flow<Result<Pair<List<Folder>, List<Asset>>>> = flow {
        emit(getPairedItems(TEST_TOKEN, "root"))
    }.flowOn(dispatcher.io).asResult()

    override suspend fun deleteFolder(folderId: String) {
        withContext(dispatcher.io) {
            foldersRemoteDataSource.deleteFolder(TEST_TOKEN, folderId)
        }
    }

    override suspend fun saveFolder(folder: Folder, parentId: String?) {
        withContext(dispatcher.io) {
            if (parentId != null) foldersRemoteDataSource.createFolder(TEST_TOKEN, folder, parentId)
            else foldersRemoteDataSource.updateFolder(TEST_TOKEN, folder)
        }
    }
}