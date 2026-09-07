package com.anahit.mediaplayer.core.media

import com.anahit.mediaplayer.domain.model.MediaItem
import kotlinx.coroutines.flow.StateFlow

/**
 * The app-facing seam over Media3: everything downstream of this interface talks in domain
 * [MediaItem]s and plain Kotlin types, never in `androidx.media3.*` classes.
 */
interface PlayerController {
    val playbackState: StateFlow<PlaybackState>

    fun setQueue(
        items: List<MediaItem>,
        startIndex: Int,
    )

    fun playPause()

    fun seekTo(positionMillis: Long)

    fun seekForward()

    fun seekBack()

    fun skipToNext()

    fun skipToPrevious()

    fun setRepeatMode(mode: RepeatMode)
}
