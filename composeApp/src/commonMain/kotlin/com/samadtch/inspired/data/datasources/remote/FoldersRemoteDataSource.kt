package com.samadtch.inspired.data.datasources.remote

import com.samadtch.inspired.data.datasources.remote.dto.FolderItemDTO

interface FoldersRemoteDataSource {

    suspend fun getFolderItems(
        token: String,
        folderId: String = "root"
    ): List<FolderItemDTO>

}