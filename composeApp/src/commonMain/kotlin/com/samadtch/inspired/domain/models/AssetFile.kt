package com.samadtch.inspired.domain.models

import androidx.compose.ui.graphics.ImageBitmap

data class AssetFile(
    val fileName: String,
    val bitmap: ImageBitmap
)
