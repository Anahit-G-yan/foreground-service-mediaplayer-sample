package com.mediaplayer.app.core.media.mapper

import com.mediaplayer.app.domain.model.Track
import com.mediaplayer.app.domain.model.Video
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MediaItemMapperTest {
    @Test
    fun `track maps id, title and artist`() {
        val track =
            Track(id = "1", title = "Song", path = "/music/song.mp3", durationMillis = 120_000, artist = "Artist")

        val playerItem = track.toPlayerItem()

        assertEquals("1", playerItem.mediaId)
        assertEquals("Song", playerItem.mediaMetadata.title.toString())
        assertEquals("Artist", playerItem.mediaMetadata.artist.toString())
    }

    @Test
    fun `video maps id and title without an artist`() {
        val video = Video(id = "2", title = "Clip", path = "/movies/clip.mp4", durationMillis = 60_000)

        val playerItem = video.toPlayerItem()

        assertEquals("2", playerItem.mediaId)
        assertEquals("Clip", playerItem.mediaMetadata.title.toString())
        assertNull(playerItem.mediaMetadata.artist)
    }
}
