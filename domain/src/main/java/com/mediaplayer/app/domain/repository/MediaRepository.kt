package com.mediaplayer.app.domain.repository

import com.mediaplayer.app.core.common.Outcome
import com.mediaplayer.app.domain.model.Track
import com.mediaplayer.app.domain.model.Video

/**
 * Reads audio/video items visible to the app through the device's media store. Wrapped in
 * [Outcome] because it is a real I/O boundary (content resolver query) that can fail.
 */
interface MediaRepository {
    suspend fun getTracks(): Outcome<List<Track>>

    suspend fun getVideos(): Outcome<List<Video>>
}
