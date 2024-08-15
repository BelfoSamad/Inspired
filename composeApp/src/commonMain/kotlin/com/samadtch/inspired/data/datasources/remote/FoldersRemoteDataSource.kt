package com.samadtch.inspired.data.datasources.remote

import com.samadtch.inspired.data.datasources.remote.dto.FolderDTO
import com.samadtch.inspired.data.datasources.remote.dto.FolderItemDTO
import com.samadtch.inspired.domain.models.Folder

interface FoldersRemoteDataSource {

    suspend fun getFolderItems(
        token: String,
        folderId: String = "root"
    ): List<FolderItemDTO>

    suspend fun deleteFolder(token: String, folderId: String)

    suspend fun createFolder(token: String, folder: Folder): FolderDTO

    suspend fun updateFolder(token: String, folder: Folder): FolderDTO

}