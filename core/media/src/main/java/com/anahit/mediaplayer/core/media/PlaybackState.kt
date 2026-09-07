package com.anahit.mediaplayer.core.media

data class PlaybackState(
    val currentItemId: String? = null,
    val currentItemTitle: String = "",
    val currentItemSubtitle: String? = null,
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
