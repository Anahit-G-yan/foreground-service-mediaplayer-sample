package com.anahit.mediaplayer.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(quality: Int = 100): ByteArray {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
    return outputStream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

//getting media duration
fun String.durationToInt(): Int {
    val duration = Integer.parseInt(this) / 1000
    return duration
}

fun Int.currentPositionIntoSeconds(): Int {
    val duration = this / 1000
    return duration
}

//converting duration into minutes and seconds
fun String.convertingIntoMinutesAndSeconds(): String {
    val duration = Integer.parseInt(this) / 1000
    val seconds = duration % 60
    val minutes = duration / 60
    return if (seconds < 10) {
        "$minutes:0$seconds"
    } else {
        "$minutes:$seconds"
    }
}

//getting system thumbnails for media files, if they exist
fun String.thumbnailToByteArray(): ByteArray? {
    val mediaMetadataRetriever = MediaMetadataRetriever()
    mediaMetadataRetriever.setDataSource(this)
    val art: ByteArray? = mediaMetadataRetriever.embeddedPicture
    mediaMetadataRetriever.release()
    return art
}




