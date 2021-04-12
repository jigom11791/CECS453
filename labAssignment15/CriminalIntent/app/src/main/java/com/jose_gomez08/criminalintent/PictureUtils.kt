package com.jose_gomez08.criminalintent

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import kotlin.math.roundToInt

fun jgGetScaledBitmap(jgPath: String, jgActivity: Activity): Bitmap {
    val jgSize = Point()
    jgActivity.windowManager.defaultDisplay.getSize(jgSize)
    return jgGetScaledBitmap(jgPath, jgSize.x, jgSize.y)
}

fun jgGetScaledBitmap(jgPath: String, jgDestWidth: Int, jgDestHeight: Int): Bitmap {
// Read in the dimensions of the image on disk
    var jgOptions = BitmapFactory.Options()
    jgOptions.inJustDecodeBounds = true
    BitmapFactory.decodeFile(jgPath, jgOptions)
    val jgSrcWidth = jgOptions.outWidth.toFloat()
    val jgSrcHeight = jgOptions.outHeight.toFloat()
// Figure out how much to scale down by
    var jgInSampleSize = 1
    if (jgSrcHeight > jgDestHeight || jgSrcWidth > jgDestWidth) {
        val heightScale = jgSrcHeight / jgDestHeight
        val widthScale = jgSrcWidth / jgDestWidth
        val sampleScale = if (heightScale > widthScale) {
            heightScale
        } else {
            widthScale
        }
        jgInSampleSize = sampleScale.roundToInt()
    }
    jgOptions = BitmapFactory.Options()
    jgOptions.inSampleSize = jgInSampleSize
// Read in and create final bitmap
    return BitmapFactory.decodeFile(jgPath, jgOptions)
}