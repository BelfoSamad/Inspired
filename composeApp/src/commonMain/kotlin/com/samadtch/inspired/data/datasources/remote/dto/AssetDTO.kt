package com.samadtch.inspired.data.datasources.remote.dto

import com.samadtch.inspired.domain.models.Asset
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssetUploadJobDTO(
    val job: AssetUploadDTO
)

@Serializable
data class AssetUploadDTO(
    val id: String? = null,
    val status: String? = null,//failed, in_progress, success
    val error: AssetUploadError? = null,
    val asset: AssetDTO? = null
)

@Serializable
data class AssetUploadError(
    val code: String, //file_too_big, import_failed
    val message: String
)

@Serializable
data class AssetInput(
    val name: String,
    val tags: List<String>
)

@Serializable
data class AssetDTO(
    val id: String,
    val name: String,
    val tags: List<String>,
    @SerialName("created_at")
    val createdAt: Int,
    @SerialName("updated_at")
    val updatedAt: Int
)

fun AssetDTO.asExternalModel() = Asset(
    assetId = id,
    name = name,
    tags = tags,
    createdAt = Instant.fromEpochSeconds(createdAt.toLong())
)