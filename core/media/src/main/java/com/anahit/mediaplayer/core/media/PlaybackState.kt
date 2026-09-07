package com.anahit.mediaplayer.core.media

import com.anahit.mediaplayer.domain.model.MediaItem

data class PlaybackState(
    val currentItem: MediaItem? = null,
    val isPlaying: Boolean = false,
    val positionMillis: Long = 0L,
    val durationMillis: Long = 0L,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
)

enum class RepeatMode {
    OFF,
    ONE,
    ALL,
}
