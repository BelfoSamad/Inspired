package com.samadtch.inspired.data.repositories

import com.samadtch.inspired.domain.models.Asset

interface AssetsRepository {

    suspend fun createAsset(asset: Asset)

    suspend fun deleteAsset(assetId: String)

}