package com.samadtch.inspired.data.repositories.impl

import androidx.compose.ui.graphics.ImageBitmap
import com.samadtch.inspired.common.di.Dispatcher
import com.samadtch.inspired.data.datasources.remote.AssetsRemoteDataSource
import com.samadtch.inspired.data.repositories.AssetsRepository
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.utilities.getPaletteImage
import kotlinx.coroutines.withContext

class AssetsRepositoryImpl(
    private val assetsRemoteDataSource: AssetsRemoteDataSource,
    private val dispatcher: Dispatcher
) : AssetsRepository {

    //TODO: Get Proper Token
    private val TEST_TOKEN = "eyJraWQiOiIyMzY4ZjRhYi00N2ZiLTQwN2MtYjM5Ni00NzgxODcwMjZkN2UiLCJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJSMEpLQXg5OHVkYVIyXzJySkx6YlZ3IiwiY2xpZW50X2lkIjoiT0MtQVpFaDJsTWhvNTlfIiwiYXVkIjoiaHR0cHM6Ly93d3cuY2FudmEuY29tIiwiaWF0IjoxNzIzMzcwMzM2LCJuYmYiOjE3MjMzNzAzMzYsImV4cCI6MTcyMzM4NDczNiwiYnVuZGxlcyI6W10sInRpZXIiOiJmcmVlIiwicm9sZXMiOiJzeFVLSlp0Mmc3ckhYY182REdiVVV2b2FmeURjWDQxM1VxVXE0alkzQ2V5dk1YWGZTanBLT3hyQV9HcDI0WkxRWnZRWjZrMDFRMDE1ZzFqUEpleUJ1ZFRiUkFJTGhLOFpvM2tvZzFZUlVpNHg5VWRuR1Z6RUc4V0lNcHJyc2VIS2wxME04S2lVRHRKeUxGa1ZlTVY5SWFvbUpGNCIsImxvY2FsZSI6ImNvTlExa1U1cWV2R3BGdUV0cUxlcHFianYxTlpLTlhDM2VVWGtjRVU2UEdSOFdHQktRVm9rQUdoTDhMeUdza3ZJcVNnSkEiLCJzY29wZXMiOlsiZm9sZGVyOnJlYWQiLCJmb2xkZXI6d3JpdGUiLCJhc3NldDpyZWFkIiwiYXNzZXQ6d3JpdGUiLCJwcm9maWxlOnJlYWQiLCJjb21tZW50OnJlYWQiLCJjb21tZW50OndyaXRlIl0sInN1YiI6Im9VVlVOa0hMZGFJSVZRZVZxV0l1bzAiLCJicmFuZCI6Im9CVlVONEhZeHo1THZndG40X3ZDR3MiLCJvcmdhbml6YXRpb24iOm51bGwsImNvZGVfaWQiOiJUUEdFSFVsT253eElnMXlfbWFKSlhnIn0.GV9HfmGhE5iJiVOH6hsga4Qv9sQIoSoRI2vQmBHwb59AYYQqwfqUpx8qvIpE2hk4gf6jen3WqSjGQjiCKiuk8kSQa4GoL2zb6uhh3jQGATgnPyXUPVGNtLjylVndh07HYUgCq8rtCK-GX3nGPOCe_C55aj5sgqJGui2JVjXNwx50E-iYgbzkLi9x9ZkB_AM8KDRX1jLfDYpSehVjpPku79xBs7fs1E6Y8IyWeUR8LumGWhAH3Y4Tkf11VwLWcoclHwDuLsESjq3jc-lIN1H9zk3W9f_wMmCsIrrs1YVFfr0vmyPl2VpBQuQrhv8Xn27OulEi1ZR68ytktdlG6-7E8w"

    override suspend fun createAsset(asset: Asset) {
        withContext(dispatcher.io) {
            var imageBitmap: ImageBitmap = asset.assetFile!!.bitmap

            //Recreate Bitmap if type is palette
            println(asset.tags)
            if (asset.tags.contains("palette")) imageBitmap = getPaletteImage(imageBitmap)

            //Create Asset
            assetsRemoteDataSource.createAsset(
                TEST_TOKEN,
                asset.copy(assetFile = asset.assetFile.copy(bitmap = imageBitmap)) //replace bitmap
            )
        }
    }

    override suspend fun deleteAsset(assetId: String) {
        withContext(dispatcher.io) {
            assetsRemoteDataSource.deleteAsset(TEST_TOKEN, assetId)
        }
    }

}