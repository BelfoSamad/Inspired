package com.samadtch.inspired.data.datasources.remote.impl

import com.samadtch.inspired.common.exceptions.handleDataError
import com.samadtch.inspired.data.datasources.remote.FoldersRemoteDataSource
import com.samadtch.inspired.data.datasources.remote.dto.FolderItemDTO
import com.samadtch.inspired.data.datasources.remote.dto.FolderItemsDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get

class FoldersRemoteDataSourceImpl(
    private val client: HttpClient
) : FoldersRemoteDataSource {

    override suspend fun getFolderItems(token: String, folderId: String): List<FolderItemDTO> =
        handleDataError("getFolderItems") {
            val folderItems = mutableListOf<FolderItemDTO>()
            val items = client.get("folders/$folderId/items?item_types=asset,folder") {
                bearerAuth(token)
            }.body<FolderItemsDTO>()
            folderItems.addAll(items.items)

            //Get More Items
            var continuationToken = items.continuation
            while (continuationToken != null) {
                val newItems = client.get("folders/$folderId/items?item_types=asset,folder&continuation=${continuationToken}")
                    { bearerAuth(token) }.body<FolderItemsDTO>()
                folderItems.addAll(newItems.items)
                continuationToken = newItems.continuation
            }

            folderItems
        }
}