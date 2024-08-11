package com.samadtch.inspired.domain.utilities

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun convertBitmapToByteArray(bitmap: ImageBitmap): ByteArray {
    val androidBitmap = bitmap.asAndroidBitmap()
    return ByteArrayOutputStream().use { baos ->
        androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        baos.toByteArray()
    }
}