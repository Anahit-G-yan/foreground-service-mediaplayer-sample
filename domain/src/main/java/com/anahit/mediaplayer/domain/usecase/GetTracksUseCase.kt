package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.core.common.Outcome
import com.anahit.mediaplayer.domain.model.Track
import com.anahit.mediaplayer.domain.repository.MediaRepository

class GetTracksUseCase(
    private val mediaRepository: MediaRepository,
) {
    suspend operator fun invoke(): Outcome<List<Track>> = mediaRepository.getTracks()
}
