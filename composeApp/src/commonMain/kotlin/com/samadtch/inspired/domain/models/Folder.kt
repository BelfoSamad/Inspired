package com.samadtch.inspired.domain.models

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Folder @OptIn(ExperimentalTime::class) constructor(
    val folderId: String? = null,
    val name: String,
    val parentId: String? = null,
    val children: List<Folder>? = null,
    val createdAt: Instant? = null
)
