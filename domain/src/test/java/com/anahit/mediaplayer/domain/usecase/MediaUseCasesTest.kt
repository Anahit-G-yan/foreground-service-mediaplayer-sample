package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.core.common.Outcome
import com.anahit.mediaplayer.domain.model.Track
import com.anahit.mediaplayer.domain.model.Video
import com.anahit.mediaplayer.domain.repository.MediaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeMediaRepository(
    private val tracks: Outcome<List<Track>> = Outcome.Success(emptyList()),
    private val videos: Outcome<List<Video>> = Outcome.Success(emptyList()),
) : MediaRepository {
    override suspend fun getTracks(): Outcome<List<Track>> = tracks

    override suspend fun getVideos(): Outcome<List<Video>> = videos
}

class MediaUseCasesTest {
    private val track =
        Track(
            id = "1",
            title = "Song",
            path = "/music/song.mp3",
            durationMillis = 120_000,
            artist = "Artist",
        )
    private val video = Video(id = "2", title = "Clip", path = "/movies/clip.mp4", durationMillis = 60_000)

    @Test
    fun `GetTracksUseCase delegates to repository`() =
        runTest {
            val useCase = GetTracksUseCase(FakeMediaRepository(tracks = Outcome.Success(listOf(track))))

            val result = useCase()

            assertEquals(Outcome.Success(listOf(track)), result)
        }

    @Test
    fun `GetTracksUseCase surfaces repository failure`() =
        runTest {
            val failure = Outcome.Error(IllegalStateException("boom"))
            val useCase = GetTracksUseCase(FakeMediaRepository(tracks = failure))

            val result = useCase()

            assertSame(failure, result)
        }

    @Test
    fun `GetVideosUseCase delegates to repository`() =
        runTest {
            val useCase = GetVideosUseCase(FakeMediaRepository(videos = Outcome.Success(listOf(video))))

            val result = useCase()

            assertTrue(result is Outcome.Success)
            assertEquals(listOf(video), (result as Outcome.Success).data)
        }
}
