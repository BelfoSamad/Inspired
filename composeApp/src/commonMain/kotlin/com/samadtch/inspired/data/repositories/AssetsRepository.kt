package com.samadtch.inspired.data.repositories

import com.samadtch.inspired.domain.models.Asset

interface AssetsRepository {

    suspend fun createAsset(token: String, asset: Asset)

    suspend fun deleteAsset(token: String, assetId: String)

}