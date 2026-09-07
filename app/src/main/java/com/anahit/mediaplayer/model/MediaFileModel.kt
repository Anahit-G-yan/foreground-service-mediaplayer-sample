package com.anahit.mediaplayer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaFileModel(
        var id: String,
        var title: String,
        var path: String,
        var duration: String,
        var byteArray: ByteArray? = null,
        var artist: String? = null
) : Parcelable