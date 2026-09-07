package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.domain.repository.PlaybackSettingsRepository
import javax.inject.Inject

class SetForegroundPlaybackUseCase
    @Inject
    constructor(
        private val playbackSettingsRepository: PlaybackSettingsRepository,
    ) {
        suspend operator fun invoke(enabled: Boolean) = playbackSettingsRepository.setForegroundPlaybackEnabled(enabled)
    }
