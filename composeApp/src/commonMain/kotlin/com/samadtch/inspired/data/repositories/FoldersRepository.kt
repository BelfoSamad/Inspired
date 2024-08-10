package com.samadtch.inspired.data.repositories

import com.samadtch.inspired.common.Result
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.Folder
import kotlinx.coroutines.flow.Flow

interface FoldersRepository {

    fun getAllItems(): Flow<Result<Pair<List<Folder>, List<Asset>>>>

}