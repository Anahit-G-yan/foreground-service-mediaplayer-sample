package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.domain.repository.PlaybackSettingsRepository

class SetForegroundPlaybackUseCase(
    private val playbackSettingsRepository: PlaybackSettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) = playbackSettingsRepository.setForegroundPlaybackEnabled(enabled)
}
