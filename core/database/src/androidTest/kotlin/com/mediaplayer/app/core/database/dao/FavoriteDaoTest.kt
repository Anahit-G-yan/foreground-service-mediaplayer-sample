package com.mediaplayer.app.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.mediaplayer.app.core.database.AppDatabase
import com.mediaplayer.app.core.database.entity.FavoriteEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private fun favoriteEntity(
    trackId: String,
    title: String = "Song $trackId",
) = FavoriteEntity(
    trackId = trackId,
    title = title,
    path = "/music/$trackId.mp3",
    durationMillis = 120_000,
    artist = "Artist",
    artwork = null,
)

@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: FavoriteDao

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = database.favoriteDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertedFavoriteIsReturnedByObserveAll() =
        runTest {
            dao.insert(favoriteEntity("1"))

            dao.observeAll().test {
                val favorites = awaitItem()
                assertEquals(1, favorites.size)
                assertEquals("1", favorites.single().trackId)
            }
        }

    @Test
    fun favoritesAreOrderedByTitle() =
        runTest {
            dao.insert(favoriteEntity("1", title = "Zebra"))
            dao.insert(favoriteEntity("2", title = "Apple"))

            dao.observeAll().test {
                val titles = awaitItem().map { it.title }
                assertEquals(listOf("Apple", "Zebra"), titles)
            }
        }

    @Test
    fun deleteByTrackIdRemovesOnlyThatFavorite() =
        runTest {
            dao.insert(favoriteEntity("1"))
            dao.insert(favoriteEntity("2"))

            dao.deleteByTrackId("1")

            dao.observeAll().test {
                val remaining = awaitItem()
                assertEquals(1, remaining.size)
                assertEquals("2", remaining.single().trackId)
            }
        }

    @Test
    fun insertingTheSameTrackIdReplacesTheExistingRow() =
        runTest {
            dao.insert(favoriteEntity("1", title = "Original"))
            dao.insert(favoriteEntity("1", title = "Updated"))

            dao.observeAll().test {
                val favorites = awaitItem()
                assertEquals(1, favorites.size)
                assertEquals("Updated", favorites.single().title)
            }
        }

    @Test
    fun observeAllStartsEmpty() =
        runTest {
            dao.observeAll().test {
                assertTrue(awaitItem().isEmpty())
            }
        }
}
