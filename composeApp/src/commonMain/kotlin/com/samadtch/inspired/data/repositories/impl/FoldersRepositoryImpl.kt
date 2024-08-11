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
    private val TEST_TOKEN = "eyJraWQiOiIyMzY4ZjRhYi00N2ZiLTQwN2MtYjM5Ni00NzgxODcwMjZkN2UiLCJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJNRXFkTlBhcWpVYURYV2t2RXlTbGFBIiwiY2xpZW50X2lkIjoiT0MtQVpFaDJsTWhvNTlfIiwiYXVkIjoiaHR0cHM6Ly93d3cuY2FudmEuY29tIiwiaWF0IjoxNzIzMzUzNzkyLCJuYmYiOjE3MjMzNTM3OTIsImV4cCI6MTcyMzM2ODE5MiwiYnVuZGxlcyI6W10sInRpZXIiOiJmcmVlIiwicm9sZXMiOiJZQ2dCSUYtcUVtS0puUEIzZ29ZQS1vYkFiTnlOdF9YclRva3kyLUFQODEtVGtfeThsaDNHSkRWdk9oZE5EVzVHMVFpeERIbGNfUUhxTXNHMHdMaHhJNV9yRDRlNUNMTmdObkdQdWZrall2d3RHdS0wZXlkTTI0YWRTYnIyR1o4N0t6YkRRRXlLbllwb05Yd203OUppZ3l0NUJiVSIsImxvY2FsZSI6InIxS0pvUDNWc2VOanNoRTRhUVk4MFBJcnk0QzNQa3dUa0RTOUNXNzJrV0RUYXVHZDE4eVFsMEZIV1JqRUtzb0NlY3VtNnciLCJzY29wZXMiOlsiZm9sZGVyOnJlYWQiLCJmb2xkZXI6d3JpdGUiLCJhc3NldDpyZWFkIiwiYXNzZXQ6d3JpdGUiLCJwcm9maWxlOnJlYWQiLCJjb21tZW50OnJlYWQiLCJjb21tZW50OndyaXRlIl0sInN1YiI6Im9VVlVOa0hMZGFJSVZRZVZxV0l1bzAiLCJicmFuZCI6Im9CVlVONEhZeHo1THZndG40X3ZDR3MiLCJvcmdhbml6YXRpb24iOm51bGwsImNvZGVfaWQiOiJUUEdFSFVsT253eElnMXlfbWFKSlhnIn0.IMFRT9dDEEMElK9KLMFxS6VM8Agmd-2d9ZyHz5JAwztpDv-t2U4ojqSPQiC80BLfgMyGkrEaEmI88YYihzje9hV6OyZpNHK97QF8NLhXu4DSMJTlkELgsCnhAR-8kon_dIhM2NJrMah39R7e3o671eCONZ1QHhiaBBkqDN4f1cefMvYTPmee2ssQkfwCc6WYbSzk9WTf1h1tOwPbpulw4UUpkbYl2hCA2-p9nV2Gw4HF1q3PAfex0-oyvw1a1u4F6lIEhV1-fckTeytmBEGqmLpZvITNKg90Wp1YoDVxNfbPcQtN3IT34Zpn9Os2-khokQ7-MFVlxt-s5Mk3iy4SfQ"

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
        println("ASSETS: $assets")

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