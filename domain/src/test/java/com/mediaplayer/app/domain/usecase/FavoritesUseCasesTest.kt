package com.mediaplayer.app.domain.usecase

import app.cash.turbine.test
import com.mediaplayer.app.domain.model.FavoriteTrack
import com.mediaplayer.app.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

private class FakeFavoritesRepository : FavoritesRepository {
    val favorites = MutableStateFlow<List<FavoriteTrack>>(emptyList())
    val added = mutableListOf<FavoriteTrack>()
    val removed = mutableListOf<String>()

    override fun observeFavorites(): Flow<List<FavoriteTrack>> = favorites

    override suspend fun addFavorite(track: FavoriteTrack) {
        added += track
        favorites.value = favorites.value + track
    }

    override suspend fun removeFavorite(trackId: String) {
        removed += trackId
        favorites.value = favorites.value.filterNot { it.id == trackId }
    }
}

private fun favoriteTrack(id: String) =
    FavoriteTrack(
        id = id,
        title = "Song $id",
        path = "/music/$id.mp3",
        durationMillis = 120_000,
        artist = "Artist",
        artwork = null,
    )

class FavoritesUseCasesTest {
    @Test
    fun `ObserveFavoritesUseCase reflects repository stream`() =
        runTest {
            val repository = FakeFavoritesRepository()
            val useCase = ObserveFavoritesUseCase(repository)

            useCase().test {
                assertEquals(emptyList<FavoriteTrack>(), awaitItem())

                repository.favorites.value = listOf(favoriteTrack("1"))

                assertEquals(listOf("1"), awaitItem().map { it.id })
            }
        }

    @Test
    fun `AddFavoriteUseCase forwards to repository`() =
        runTest {
            val repository = FakeFavoritesRepository()
            val useCase = AddFavoriteUseCase(repository)
            val track = favoriteTrack("1")

            useCase(track)

            assertEquals(listOf(track), repository.added)
        }

    @Test
    fun `RemoveFavoriteUseCase forwards to repository`() =
        runTest {
            val repository = FakeFavoritesRepository()
            val useCase = RemoveFavoriteUseCase(repository)

            useCase("1")

            assertEquals(listOf("1"), repository.removed)
        }
}
