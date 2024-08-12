package com.samadtch.inspired.data.repositories

import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.AssetFile

interface AssetsRepository {

    suspend fun createAsset(token: String, asset: Asset, assetFile: AssetFile)

    suspend fun deleteAsset(token: String, assetId: String)

}