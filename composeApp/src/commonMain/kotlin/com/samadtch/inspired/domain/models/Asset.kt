package com.samadtch.inspired.domain.models

import com.samadtch.inspired.data.datasources.remote.dto.Thumbnail
import kotlinx.datetime.Instant

data class Asset(
    val assetId: String? = null,
    val name: String,
    val tags: List<String>,
    val createdAt: Instant? = null,
    val folderId: String? = null,
    val assetFile: AssetFile? = null,
    val thumbnail: Thumbnail? = null
)
