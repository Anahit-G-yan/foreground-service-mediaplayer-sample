package com.anahit.mediaplayer.feature.player

import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ArtworkProvider {
    suspend fun embeddedArtwork(path: String): ByteArray?
}

class MediaMetadataArtworkProvider
    @Inject
    constructor() : ArtworkProvider {
        // A broad catch is deliberate: MediaMetadataRetriever throws on unreadable/corrupt/DRM'd
        // files, and the correct response here is just "no artwork", not a crash.
        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        override suspend fun embeddedArtwork(path: String): ByteArray? =
            withContext(Dispatchers.IO) {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(path)
                    retriever.embeddedPicture
                } catch (e: RuntimeException) {
                    null
                } finally {
                    retriever.release()
                }
            }
    }
