package com.samadtch.inspired.data.datasources.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FolderItemsDTO(
    val items: List<FolderItemDTO>,
    val continuation: String? = null
)

@Serializable
data class FolderItemDTO(
    val type: String,
    val image: AssetDTO? = null,
    val folder: FolderDTO? = null
)