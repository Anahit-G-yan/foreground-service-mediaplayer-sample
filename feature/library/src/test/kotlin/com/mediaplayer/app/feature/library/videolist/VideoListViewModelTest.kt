package com.mediaplayer.app.feature.library.videolist

import app.cash.turbine.test
import com.mediaplayer.app.core.common.Outcome
import com.mediaplayer.app.core.ui.UiState
import com.mediaplayer.app.domain.model.Track
import com.mediaplayer.app.domain.model.Video
import com.mediaplayer.app.domain.repository.MediaRepository
import com.mediaplayer.app.domain.usecase.GetVideosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private class FakeMediaRepository(
    private val videos: Outcome<List<Video>> = Outcome.Success(emptyList()),
) : MediaRepository {
    override suspend fun getTracks(): Outcome<List<Track>> = Outcome.Success(emptyList())

    override suspend fun getVideos(): Outcome<List<Video>> = videos
}

private fun video(id: String) = Video(id = id, title = "Clip $id", path = "/movies/$id.mp4", durationMillis = 60_000)

@OptIn(ExperimentalCoroutinesApi::class)
class VideoListViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads videos successfully`() =
        runTest {
            val videos = listOf(video("1"), video("2"))
            val viewModel = VideoListViewModel(GetVideosUseCase(FakeMediaRepository(Outcome.Success(videos))))

            viewModel.uiState.test {
                assertEquals(UiState.Loading, awaitItem())
                assertEquals(UiState.Success(videos), awaitItem())
            }
        }

    @Test
    fun `empty video list surfaces as UiState-Empty`() =
        runTest {
            val viewModel = VideoListViewModel(GetVideosUseCase(FakeMediaRepository(Outcome.Success(emptyList()))))

            viewModel.uiState.test {
                assertEquals(UiState.Loading, awaitItem())
                assertEquals(UiState.Empty, awaitItem())
            }
        }

    @Test
    fun `repository failure surfaces as UiState-Error`() =
        runTest {
            val failure = IllegalStateException("boom")
            val viewModel = VideoListViewModel(GetVideosUseCase(FakeMediaRepository(Outcome.Error(failure))))

            viewModel.uiState.test {
                assertEquals(UiState.Loading, awaitItem())
                assertEquals(UiState.Error(failure), awaitItem())
            }
        }
}
