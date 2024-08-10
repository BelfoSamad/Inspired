package com.samadtch.inspired.data.datasources.remote.dto

import com.samadtch.inspired.domain.models.Folder
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FolderDTO(
    val id: String,
    val name: String,
    @SerialName("created_at")
    val createdAt: Int,
    @SerialName("updated_at")
    val updatedAt: Int,
)

fun FolderDTO.asExternalModel() = Folder(
    folderId = id,
    name = name,
    createdAt = Instant.fromEpochSeconds(createdAt.toLong())
)