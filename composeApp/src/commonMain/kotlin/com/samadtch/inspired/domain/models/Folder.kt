package com.samadtch.inspired.domain.models

import kotlinx.datetime.Instant

data class Folder(
    val folderId: String? = null,
    val name: String,
    val parentId: String? = null,
    val children: List<Folder>? = null,
    val createdAt: Instant? = null
)
