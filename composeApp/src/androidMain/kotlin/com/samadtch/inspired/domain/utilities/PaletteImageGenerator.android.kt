package com.samadtch.inspired.domain.utilities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.palette.graphics.Palette

actual fun getPaletteImage(image: ImageBitmap): ImageBitmap {
    val palette = Palette.from(image.asAndroidBitmap()).generate()
    val colors = listOfNotNull(
        palette.vibrantSwatch?.rgb,
        palette.mutedSwatch?.rgb,
        palette.lightVibrantSwatch?.rgb,
        palette.darkVibrantSwatch?.rgb,
        palette.lightMutedSwatch?.rgb,
        palette.darkMutedSwatch?.rgb
    )
    return createColorBitmap(colors).asImageBitmap()
}

fun createColorBitmap(colors: List<Int>): Bitmap {
    // Constants
    val columnWidth = 512 // Width of each column in pixels
    val bitmapHeight = 1024 // Height of the entire bitmap in pixels
    val textPadding = 32 // Padding below the text in pixels
    val fontSize = 48f // Font size for the text in pixels

    // Create the Bitmap and Canvas
    val bitmap =
        Bitmap.createBitmap(columnWidth * colors.size, bitmapHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Paint setup for colors and text
    val paintColor = Paint().apply {
        style = Paint.Style.FILL
    }

    val paintText = Paint().apply {
        color = Color.WHITE
        textSize = fontSize
        textAlign = Paint.Align.CENTER
    }

    // Draw each column with its color and text
    colors.forEachIndexed { index, color ->
        // Draw the column
        paintColor.color = color
        val left = index * columnWidth
        val right = left + columnWidth
        canvas.drawRect(left.toFloat(), 0f, right.toFloat(), bitmapHeight.toFloat(), paintColor)

        // Draw the text
        val text = String.format("#%06X", 0xFFFFFF and color)
        val textHeight = (paintText.descent() - paintText.ascent()).toInt()
        val textY = bitmapHeight - textPadding - textHeight / 2
        canvas.drawText(text, (left + right) / 2f, textY.toFloat(), paintText)
    }

    return bitmap
}