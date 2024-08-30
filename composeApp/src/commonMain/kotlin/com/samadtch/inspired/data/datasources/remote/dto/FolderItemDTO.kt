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
    val asset: AssetDTO? = null,
    val image: AssetDTO? = null, // same as asset
    val folder: FolderDTO? = null
)