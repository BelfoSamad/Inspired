package com.samadtch.inspired.data.datasources.remote

import com.samadtch.inspired.domain.models.Asset

interface AssetsRemoteDataSource {

    suspend fun createAsset(token: String, asset: Asset)

    suspend fun deleteAsset(token: String, assetId: String)

}