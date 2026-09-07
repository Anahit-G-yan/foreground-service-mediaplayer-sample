package com.anahit.mediaplayer.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Whether music should keep playing as a foreground service (with a notification and playback
 * surviving the app being backgrounded) or stop as soon as the app leaves the foreground.
 */
interface PlaybackSettingsRepository {
    fun observeForegroundPlaybackEnabled(): Flow<Boolean>

    suspend fun setForegroundPlaybackEnabled(enabled: Boolean)
}
