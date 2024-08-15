package com.samadtch.inspired.data.repositories

import com.samadtch.inspired.common.Result
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.Folder
import kotlinx.coroutines.flow.Flow

interface FoldersRepository {

    fun getAllItems(token: String): Flow<Result<Pair<List<Folder>, List<Asset>>>>

    suspend fun deleteFolder(token: String, fId: String)

    suspend fun saveFolder(token: String, folder: Folder): Folder

}