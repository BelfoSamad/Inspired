package com.samadtch.inspired.data.datasources.remote.impl

import com.samadtch.inspired.common.exceptions.DataException
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_FILE_TOO_BIG
import com.samadtch.inspired.common.exceptions.DataException.Companion.API_ERROR_IMPORT_FAILED
import com.samadtch.inspired.common.exceptions.handleDataError
import com.samadtch.inspired.data.datasources.remote.AssetsRemoteDataSource
import com.samadtch.inspired.data.datasources.remote.dto.AssetDTO
import com.samadtch.inspired.data.datasources.remote.dto.AssetInput
import com.samadtch.inspired.data.datasources.remote.dto.AssetResponse
import com.samadtch.inspired.data.datasources.remote.dto.AssetUploadJobDTO
import com.samadtch.inspired.data.datasources.remote.dto.FolderMove
import com.samadtch.inspired.domain.models.Asset
import com.samadtch.inspired.domain.models.AssetFile
import com.samadtch.inspired.domain.utilities.convertBitmapToByteArray
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AssetsRemoteDataSourceImpl(
    private val client: HttpClient
) : AssetsRemoteDataSource {

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun createAsset(token: String, asset: Asset, assetFile: AssetFile): AssetDTO? {
        return handleDataError("createAsset") {
            //Upload File
            var assetUploadJob: AssetUploadJobDTO = client.post("asset-uploads") {
                contentType(ContentType.Application.OctetStream)
                bearerAuth(token)
                header(
                    "Asset-Upload-Metadata",
                    Json.encodeToString(mapOf("name_base64" to Base64.encode(asset.name.encodeToByteArray())))
                )
                setBody(convertBitmapToByteArray(assetFile.bitmap))
            }.body<AssetUploadJobDTO>()

            //Variables
            var newAsset: AssetDTO? = null
            var uploading = true

            //Update Asset & move to appropriate folder
            while (uploading) {
                when (assetUploadJob.job.status) {
                    "in_progress" -> {
                        delay(2000)
                        assetUploadJob = client.get("asset-uploads/${assetUploadJob.job.id}") {
                            bearerAuth(token)
                        }.body()

                        //still uploading
                        uploading = true
                    }

                    "success" -> {
                        val createdAsset = assetUploadJob.job.asset!!

                        //Update Asset (Add Tags)
                        newAsset = client.patch("assets/${createdAsset.id}") {
                            contentType(ContentType.Application.Json)
                            bearerAuth(token)
                            setBody(
                                AssetInput(
                                    name = asset.name,
                                    tags = asset.tags
                                )
                            )
                        }.body<AssetResponse>().asset

                        //Move Asset to Folder
                        if (asset.folderId!! != "root")
                            client.post("folders/move") {
                                contentType(ContentType.Application.Json)
                                bearerAuth(token)
                                setBody(
                                    FolderMove(
                                        itemId = assetUploadJob.job.asset!!.id,
                                        from = "root",
                                        to = asset.folderId
                                    )
                                )
                            }

                        //uploaded!
                        uploading = false
                    }

                    "failed" -> {
                        val error = assetUploadJob.job.error!!
                        if (error.code == "file_too_big") throw DataException(API_ERROR_FILE_TOO_BIG)
                        else throw DataException(API_ERROR_IMPORT_FAILED)
                    }
                }
            }
            newAsset
        }
    }

    override suspend fun deleteAsset(token: String, assetId: String) {
        handleDataError("deleteAsset") {
            client.delete("assets/$assetId") { bearerAuth(token) }
        }
    }

}