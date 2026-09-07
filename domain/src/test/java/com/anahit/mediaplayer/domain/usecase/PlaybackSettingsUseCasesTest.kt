package com.anahit.mediaplayer.domain.usecase

import app.cash.turbine.test
import com.anahit.mediaplayer.domain.repository.PlaybackSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

private class FakePlaybackSettingsRepository : PlaybackSettingsRepository {
    val enabled = MutableStateFlow(false)

    override fun observeForegroundPlaybackEnabled(): Flow<Boolean> = enabled

    override suspend fun setForegroundPlaybackEnabled(enabled: Boolean) {
        this.enabled.value = enabled
    }
}

class PlaybackSettingsUseCasesTest {
    @Test
    fun `ObserveForegroundPlaybackUseCase reflects repository stream`() =
        runTest {
            val repository = FakePlaybackSettingsRepository()
            val useCase = ObserveForegroundPlaybackUseCase(repository)

            useCase().test {
                assertEquals(false, awaitItem())

                repository.enabled.value = true

                assertEquals(true, awaitItem())
            }
        }

    @Test
    fun `SetForegroundPlaybackUseCase forwards to repository`() =
        runTest {
            val repository = FakePlaybackSettingsRepository()
            val useCase = SetForegroundPlaybackUseCase(repository)

            useCase(true)

            assertEquals(true, repository.enabled.value)
        }
}
