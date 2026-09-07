package com.anahit.mediaplayer.domain.repository

import com.anahit.mediaplayer.core.common.Outcome
import com.anahit.mediaplayer.domain.model.Track
import com.anahit.mediaplayer.domain.model.Video

/**
 * Reads audio/video items visible to the app through the device's media store. Wrapped in
 * [Outcome] because it is a real I/O boundary (content resolver query) that can fail.
 */
interface MediaRepository {
    suspend fun getTracks(): Outcome<List<Track>>

    suspend fun getVideos(): Outcome<List<Video>>
}
