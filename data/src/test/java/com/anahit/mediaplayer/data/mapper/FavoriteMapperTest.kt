package com.anahit.mediaplayer.data.mapper

import com.anahit.mediaplayer.core.database.entity.FavoriteEntity
import com.anahit.mediaplayer.domain.model.FavoriteTrack
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteMapperTest {
    @Test
    fun `entity maps to domain preserving all fields`() {
        val entity =
            FavoriteEntity(
                trackId = "1",
                title = "Song",
                path = "/music/song.mp3",
                durationMillis = 120_000,
                artist = "Artist",
                artwork = byteArrayOf(1, 2, 3),
            )

        val domain = entity.toDomain()

        assertEquals(entity.trackId, domain.id)
        assertEquals(entity.title, domain.title)
        assertEquals(entity.path, domain.path)
        assertEquals(entity.durationMillis, domain.durationMillis)
        assertEquals(entity.artist, domain.artist)
        assertArrayEquals(entity.artwork, domain.artwork)
    }

    @Test
    fun `domain maps to entity preserving all fields`() {
        val track =
            FavoriteTrack(
                id = "1",
                title = "Song",
                path = "/music/song.mp3",
                durationMillis = 120_000,
                artist = "Artist",
                artwork = byteArrayOf(1, 2, 3),
            )

        val entity = track.toEntity()

        assertEquals(track.id, entity.trackId)
        assertEquals(track.title, entity.title)
        assertEquals(track.path, entity.path)
        assertEquals(track.durationMillis, entity.durationMillis)
        assertEquals(track.artist, entity.artist)
        assertArrayEquals(track.artwork, entity.artwork)
    }

    @Test
    fun `round trip preserves data`() {
        val original =
            FavoriteTrack(
                id = "1",
                title = "Song",
                path = "/music/song.mp3",
                durationMillis = 120_000,
                artist = null,
                artwork = null,
            )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.title, roundTripped.title)
        assertEquals(original.path, roundTripped.path)
        assertEquals(original.durationMillis, roundTripped.durationMillis)
        assertEquals(original.artist, roundTripped.artist)
    }
}
