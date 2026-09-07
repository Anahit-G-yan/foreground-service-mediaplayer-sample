package com.anahit.mediaplayer.core.media.mapper

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.anahit.mediaplayer.domain.model.Track
import com.anahit.mediaplayer.domain.model.MediaItem as DomainMediaItem

fun DomainMediaItem.toPlayerItem(): MediaItem {
    val metadata =
        MediaMetadata
            .Builder()
            .setTitle(title)
            .setIsBrowsable(false)
            .setIsPlayable(true)
            .apply {
                if (this@toPlayerItem is Track) {
                    setArtist(artist)
                }
            }.build()

    return MediaItem
        .Builder()
        .setMediaId(id)
        .setUri(path)
        .setMediaMetadata(metadata)
        .build()
}
