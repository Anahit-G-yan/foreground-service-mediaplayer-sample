package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.domain.repository.PlaybackSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveForegroundPlaybackUseCase
    @Inject
    constructor(
        private val playbackSettingsRepository: PlaybackSettingsRepository,
    ) {
        operator fun invoke(): Flow<Boolean> = playbackSettingsRepository.observeForegroundPlaybackEnabled()
    }
