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
                    if (childItems != null) assets.addAll(childItems.second)
                }

                "asset" -> {
                    val asset = item.asset!!.asExternalModel()
                    assets.add(asset)
                }
            }
        }
        return folders to assets
    }

    override fun getAllItems(token: String): Flow<Result<Pair<List<Folder>, List<Asset>>>> = flow {
        emit(getPairedItems(token, "root"))
    }.flowOn(dispatcher.io).asResult()

    override suspend fun deleteFolder(token: String, fId: String) = withContext(dispatcher.io) {
        foldersRemoteDataSource.deleteFolder(token, fId)
    }

    override suspend fun saveFolder(token: String, folder: Folder, parentId: String?) =
        withContext(dispatcher.io) {
            if (parentId != null) foldersRemoteDataSource.createFolder(token, folder, parentId)
            else foldersRemoteDataSource.updateFolder(token, folder)
        }
}