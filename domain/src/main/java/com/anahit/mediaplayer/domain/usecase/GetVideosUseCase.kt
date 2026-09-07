package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.core.common.Outcome
import com.anahit.mediaplayer.domain.model.Video
import com.anahit.mediaplayer.domain.repository.MediaRepository

class GetVideosUseCase(
    private val mediaRepository: MediaRepository,
) {
    suspend operator fun invoke(): Outcome<List<Video>> = mediaRepository.getVideos()
}
