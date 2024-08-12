package com.samadtch.inspired.data.repositories.impl

import androidx.compose.ui.graphics.ImageBitmap
import com.samadtch.inspired.common.di.Dispatcher
import com.samadtch.inspired.data.datasources.remote.AssetsRemoteDataSource
import com.samadtch.inspired.data.repositories.AssetsRepository
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.domain.utilities.getPaletteImage
import kotlinx.coroutines.withContext

class AssetsRepositoryImpl(
    private val assetsRemoteDataSource: AssetsRemoteDataSource,
    private val dispatcher: Dispatcher
) : AssetsRepository {

    override suspend fun createAsset(token: String, asset: Asset, assetFile: AssetFile) = withContext(dispatcher.io) {
        var imageBitmap: ImageBitmap = assetFile.bitmap

        //Recreate Bitmap if type is palette
        if (asset.tags.contains("palette")) imageBitmap = getPaletteImage(imageBitmap)

        //Create Asset
        assetsRemoteDataSource.createAsset(
            token,
            asset,
            assetFile.copy(bitmap = imageBitmap) //replace bitmap
        )
    }

    override suspend fun deleteAsset(token: String, assetId: String) = withContext(dispatcher.io) {
        assetsRemoteDataSource.deleteAsset(token, assetId)
    }

}