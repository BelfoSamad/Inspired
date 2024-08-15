package com.samadtch.inspired.data.datasources.remote

import com.samadtch.inspired.data.datasources.remote.dto.AssetDTO
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.AssetFile

interface AssetsRemoteDataSource {

    suspend fun createAsset(token: String, asset: Asset, assetFile: AssetFile): AssetDTO?

    suspend fun deleteAsset(token: String, assetId: String)

}