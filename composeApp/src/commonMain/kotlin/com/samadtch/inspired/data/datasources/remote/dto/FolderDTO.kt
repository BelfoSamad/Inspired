package com.samadtch.inspired.data.datasources.remote.dto

import com.samadtch.inspired.domain.models.Folder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class FolderInput(
    val name: String,
    @SerialName("parent_folder_id")
    val parentFolderId: String? = null
)

@Serializable
data class FolderMove(
    @SerialName("item_id")
    val itemId: String,
    @SerialName("from_folder_id")
    val from: String,
    @SerialName("to_folder_id")
    val to: String
)

@Serializable
data class FolderResponse(
    val folder: FolderDTO
)

@Serializable
data class FolderDTO(
    val id: String,
    val name: String,
    @SerialName("created_at")
    val createdAt: Int,
    @SerialName("updated_at")
    val updatedAt: Int,
    val thumbnail: Thumbnail? = null
)

@OptIn(ExperimentalTime::class)
fun FolderDTO.asExternalModel() = Folder(
    folderId = id,
    name = name,
    createdAt = Instant.fromEpochSeconds(createdAt.toLong()),
    thumbnail = thumbnail,
)