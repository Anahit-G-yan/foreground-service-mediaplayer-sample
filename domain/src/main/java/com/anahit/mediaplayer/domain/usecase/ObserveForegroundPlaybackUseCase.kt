package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.domain.repository.PlaybackSettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveForegroundPlaybackUseCase(
    private val playbackSettingsRepository: PlaybackSettingsRepository,
) {
    operator fun invoke(): Flow<Boolean> = playbackSettingsRepository.observeForegroundPlaybackEnabled()
}
