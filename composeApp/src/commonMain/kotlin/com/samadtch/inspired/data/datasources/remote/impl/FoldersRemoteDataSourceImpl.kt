package com.samadtch.inspired.data.datasources.remote.impl

import com.samadtch.inspired.common.exceptions.handleDataError
import com.samadtch.inspired.data.datasources.remote.FoldersRemoteDataSource
import com.samadtch.inspired.data.datasources.remote.dto.FolderDTO
import com.samadtch.inspired.data.datasources.remote.dto.FolderInput
import com.samadtch.inspired.data.datasources.remote.dto.FolderItemDTO
import com.samadtch.inspired.data.datasources.remote.dto.FolderItemsDTO
import com.samadtch.inspired.domain.models.Folder
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

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

    override suspend fun deleteFolder(token: String, folderId: String) {
        handleDataError("deleteFolder") {
            client.delete("folders/$folderId") { bearerAuth(token) }
        }
    }

    override suspend fun createFolder(token: String, folder: Folder, parentId: String) {
        return handleDataError("createFolder") {
            client.post("folders") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(
                    FolderInput(
                        name = folder.name,
                        parentFolderId = parentId
                    )
                )
            }
        }
    }

    override suspend fun updateFolder(token: String, folder: Folder) {
        return handleDataError("updateFolder") {
            client.patch("folders/${folder.folderId}") {
                contentType(ContentType.Application.Json)
                bearerAuth(token)
                setBody(
                    FolderInput(name = folder.name)
                )
            }
        }
    }
}