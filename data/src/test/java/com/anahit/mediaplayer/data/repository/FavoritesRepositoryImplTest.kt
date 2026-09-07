package com.anahit.mediaplayer.data.repository

import app.cash.turbine.test
import com.anahit.mediaplayer.core.database.dao.FavoriteDao
import com.anahit.mediaplayer.core.database.entity.FavoriteEntity
import com.anahit.mediaplayer.domain.model.FavoriteTrack
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoritesRepositoryImplTest {
    private val dao = mockk<FavoriteDao>()
    private val repository = FavoritesRepositoryImpl(dao)

    @Test
    fun `observeFavorites maps entities to domain models`() =
        runTest {
            val entity =
                FavoriteEntity(
                    trackId = "1",
                    title = "Song",
                    path = "/music/song.mp3",
                    durationMillis = 120_000,
                    artist = "Artist",
                    artwork = null,
                )
            every { dao.observeAll() } returns flowOf(listOf(entity))

            repository.observeFavorites().test {
                val favorites = awaitItem()
                assertEquals(1, favorites.size)
                assertEquals("1", favorites.first().id)
                awaitComplete()
            }
        }

    @Test
    fun `addFavorite inserts mapped entity`() =
        runTest {
            coEvery { dao.insert(any()) } returns Unit
            val track =
                FavoriteTrack(
                    id = "1",
                    title = "Song",
                    path = "/music/song.mp3",
                    durationMillis = 120_000,
                    artist = "Artist",
                    artwork = null,
                )

            repository.addFavorite(track)

            coVerify { dao.insert(match { it.trackId == "1" }) }
        }

    @Test
    fun `removeFavorite deletes by track id`() =
        runTest {
            coEvery { dao.deleteByTrackId(any()) } returns Unit

            repository.removeFavorite("1")

            coVerify { dao.deleteByTrackId("1") }
        }
}
