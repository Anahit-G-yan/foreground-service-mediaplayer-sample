package com.mediaplayer.app.feature.library.musiclist

import app.cash.turbine.test
import com.mediaplayer.app.core.common.Outcome
import com.mediaplayer.app.core.media.PlaybackState
import com.mediaplayer.app.core.media.PlayerController
import com.mediaplayer.app.core.media.RepeatMode
import com.mediaplayer.app.core.ui.UiState
import com.mediaplayer.app.domain.model.MediaItem
import com.mediaplayer.app.domain.model.Track
import com.mediaplayer.app.domain.model.Video
import com.mediaplayer.app.domain.repository.MediaRepository
import com.mediaplayer.app.domain.usecase.GetTracksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private class FakeMediaRepository(
    private val tracks: Outcome<List<Track>> = Outcome.Success(emptyList()),
) : MediaRepository {
    override suspend fun getTracks(): Outcome<List<Track>> = tracks

    override suspend fun getVideos(): Outcome<List<Video>> = Outcome.Success(emptyList())
}

private class FakePlayerController : PlayerController {
    override val playbackState: StateFlow<PlaybackState> = MutableStateFlow(PlaybackState())
    var lastQueue: List<MediaItem>? = null
    var lastStartIndex: Int? = null

    override fun setQueue(
        items: List<MediaItem>,
        startIndex: Int,
    ) {
        lastQueue = items
        lastStartIndex = startIndex
    }

    override fun playPause() = Unit

    override fun seekTo(positionMillis: Long) = Unit

    override fun seekForward() = Unit

    override fun seekBack() = Unit

    override fun skipToNext() = Unit

    override fun skipToPrevious() = Unit

    override fun setRepeatMode(mode: RepeatMode) = Unit
}

private fun track(id: String) =
    Track(id = id, title = "Song $id", path = "/music/$id.mp3", durationMillis = 120_000, artist = "Artist")

@OptIn(ExperimentalCoroutinesApi::class)
class MusicListViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads tracks successfully`() =
        runTest {
            val tracks = listOf(track("1"), track("2"))
            val viewModel =
                MusicListViewModel(
                    GetTracksUseCase(FakeMediaRepository(Outcome.Success(tracks))),
                    FakePlayerController(),
                )

            viewModel.uiState.test {
                assertEquals(UiState.Loading, awaitItem())
                assertEquals(UiState.Success(tracks), awaitItem())
            }
        }

    @Test
    fun `empty track list surfaces as UiState-Empty`() =
        runTest {
            val viewModel =
                MusicListViewModel(
                    GetTracksUseCase(FakeMediaRepository(Outcome.Success(emptyList()))),
                    FakePlayerController(),
                )

            viewModel.uiState.test {
                assertEquals(UiState.Loading, awaitItem())
                assertEquals(UiState.Empty, awaitItem())
            }
        }

    @Test
    fun `repository failure surfaces as UiState-Error`() =
        runTest {
            val failure = IllegalStateException("boom")
            val viewModel =
                MusicListViewModel(
                    GetTracksUseCase(FakeMediaRepository(Outcome.Error(failure))),
                    FakePlayerController(),
                )

            viewModel.uiState.test {
                assertEquals(UiState.Loading, awaitItem())
                assertEquals(UiState.Error(failure), awaitItem())
            }
        }

    @Test
    fun `play sets the whole queue on the player starting at the tapped index`() {
        val playerController = FakePlayerController()
        val viewModel = MusicListViewModel(GetTracksUseCase(FakeMediaRepository()), playerController)
        val tracks = listOf(track("1"), track("2"), track("3"))

        viewModel.play(tracks, startIndex = 1)

        assertEquals(tracks, playerController.lastQueue)
        assertEquals(1, playerController.lastStartIndex)
    }
}
