package com.mediaplayer.app.feature.player

import app.cash.turbine.test
import com.mediaplayer.app.core.media.PlaybackState
import com.mediaplayer.app.core.media.PlayerController
import com.mediaplayer.app.core.media.RepeatMode
import com.mediaplayer.app.domain.model.FavoriteTrack
import com.mediaplayer.app.domain.model.MediaItem
import com.mediaplayer.app.domain.model.Track
import com.mediaplayer.app.domain.repository.FavoritesRepository
import com.mediaplayer.app.domain.usecase.AddFavoriteUseCase
import com.mediaplayer.app.domain.usecase.ObserveFavoritesUseCase
import com.mediaplayer.app.domain.usecase.RemoveFavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

private class FakePlayerController : PlayerController {
    val state = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = state

    var playPauseCalls = 0
    var seekForwardCalls = 0
    var seekBackCalls = 0
    var skipToNextCalls = 0
    var skipToPreviousCalls = 0
    var lastSeekTo: Long? = null
    var lastRepeatMode: RepeatMode? = null

    override fun setQueue(
        items: List<MediaItem>,
        startIndex: Int,
    ) = Unit

    override fun playPause() {
        playPauseCalls++
    }

    override fun seekTo(positionMillis: Long) {
        lastSeekTo = positionMillis
    }

    override fun seekForward() {
        seekForwardCalls++
    }

    override fun seekBack() {
        seekBackCalls++
    }

    override fun skipToNext() {
        skipToNextCalls++
    }

    override fun skipToPrevious() {
        skipToPreviousCalls++
    }

    override fun setRepeatMode(mode: RepeatMode) {
        lastRepeatMode = mode
    }
}

private class FakeFavoritesRepository : FavoritesRepository {
    val favorites = MutableStateFlow<List<FavoriteTrack>>(emptyList())

    override fun observeFavorites(): Flow<List<FavoriteTrack>> = favorites

    override suspend fun addFavorite(track: FavoriteTrack) {
        favorites.value = favorites.value + track
    }

    override suspend fun removeFavorite(trackId: String) {
        favorites.value = favorites.value.filterNot { it.id == trackId }
    }
}

private class FakeArtworkProvider(
    private val bytes: ByteArray? = null,
) : ArtworkProvider {
    override suspend fun embeddedArtwork(path: String): ByteArray? = bytes
}

private fun track(id: String) =
    Track(id = id, title = "Song $id", path = "/music/$id.mp3", durationMillis = 120_000, artist = "Artist")

private fun favoriteTrack(id: String) =
    FavoriteTrack(
        id = id,
        title = "Song $id",
        path = "/music/$id.mp3",
        durationMillis = 120_000,
        artist = "Artist",
        artwork = null,
    )

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        playerController: FakePlayerController = FakePlayerController(),
        favoritesRepository: FakeFavoritesRepository = FakeFavoritesRepository(),
        artworkProvider: ArtworkProvider = FakeArtworkProvider(),
    ) = PlayerViewModel(
        playerController = playerController,
        artworkProvider = artworkProvider,
        observeFavoritesUseCase = ObserveFavoritesUseCase(favoritesRepository),
        addFavoriteUseCase = AddFavoriteUseCase(favoritesRepository),
        removeFavoriteUseCase = RemoveFavoriteUseCase(favoritesRepository),
    )

    @Test
    fun `playbackState mirrors the controller`() =
        runTest(dispatcher) {
            val controller = FakePlayerController()
            val viewModel = viewModel(playerController = controller)

            viewModel.playbackState.test {
                assertEquals(PlaybackState(), awaitItem())
                controller.state.value = PlaybackState(currentItem = track("1"), isPlaying = true)
                assertEquals(track("1"), awaitItem().currentItem)
            }
        }

    @Test
    fun `playback controls delegate to the controller`() =
        runTest(dispatcher) {
            val controller = FakePlayerController()
            val viewModel = viewModel(playerController = controller)

            viewModel.playPause()
            viewModel.seekTo(1_000L)
            viewModel.seekForward()
            viewModel.seekBack()
            viewModel.skipToNext()
            viewModel.skipToPrevious()

            assertEquals(1, controller.playPauseCalls)
            assertEquals(1_000L, controller.lastSeekTo)
            assertEquals(1, controller.seekForwardCalls)
            assertEquals(1, controller.seekBackCalls)
            assertEquals(1, controller.skipToNextCalls)
            assertEquals(1, controller.skipToPreviousCalls)
        }

    @Test
    fun `toggleRepeatCurrentTrack switches between OFF and ONE`() =
        runTest(dispatcher) {
            val controller = FakePlayerController()
            val viewModel = viewModel(playerController = controller)

            viewModel.toggleRepeatCurrentTrack()
            assertEquals(RepeatMode.ONE, controller.lastRepeatMode)

            controller.state.value = controller.state.value.copy(repeatMode = RepeatMode.ONE)
            viewModel.toggleRepeatCurrentTrack()
            assertEquals(RepeatMode.OFF, controller.lastRepeatMode)
        }

    @Test
    fun `isCurrentTrackFavorite reflects the favorites repository for the playing track`() =
        runTest(dispatcher) {
            val favoritesRepository = FakeFavoritesRepository()
            val controller = FakePlayerController().apply { state.value = PlaybackState(currentItem = track("1")) }
            val viewModel = viewModel(playerController = controller, favoritesRepository = favoritesRepository)

            // StateFlow only re-emits on an actual change, so this starts from the track already
            // selected (favorite = false) and only asserts the one distinct transition to true.
            viewModel.isCurrentTrackFavorite.test {
                assertEquals(false, awaitItem())
                favoritesRepository.favorites.value = listOf(favoriteTrack("1"))
                assertEquals(true, awaitItem())
            }
        }

    @Test
    fun `toggleFavorite adds the current track with its embedded artwork when not favorited`() =
        runTest(dispatcher) {
            val favoritesRepository = FakeFavoritesRepository()
            val controller = FakePlayerController().apply { state.value = PlaybackState(currentItem = track("1")) }
            val artwork = byteArrayOf(1, 2, 3)
            val viewModel = viewModel(controller, favoritesRepository, FakeArtworkProvider(artwork))

            viewModel.toggleFavorite()

            assertEquals(listOf("1"), favoritesRepository.favorites.value.map { it.id })
            assertEquals(
                artwork,
                favoritesRepository.favorites.value
                    .single()
                    .artwork,
            )
        }

    @Test
    fun `toggleFavorite removes the current track when already favorited`() =
        runTest(dispatcher) {
            val favoritesRepository = FakeFavoritesRepository().apply { favorites.value = listOf(favoriteTrack("1")) }
            val controller = FakePlayerController().apply { state.value = PlaybackState(currentItem = track("1")) }
            val viewModel = viewModel(controller, favoritesRepository)

            viewModel.toggleFavorite()

            assertEquals(emptyList<FavoriteTrack>(), favoritesRepository.favorites.value)
        }

    @Test
    fun `artwork clears when there is no current track`() =
        runTest(dispatcher) {
            val controller = FakePlayerController()
            val viewModel =
                viewModel(playerController = controller, artworkProvider = FakeArtworkProvider(byteArrayOf(9)))

            viewModel.artwork.test {
                assertNull(awaitItem())
                controller.state.value = PlaybackState(currentItem = track("1"))
                assertEquals(listOf<Byte>(9), awaitItem()?.toList())
                controller.state.value = PlaybackState(currentItem = null)
                assertNull(awaitItem())
            }
        }
}
