package com.mediaplayer.app.domain.usecase

import com.mediaplayer.app.core.common.Outcome
import com.mediaplayer.app.domain.model.Video
import com.mediaplayer.app.domain.repository.MediaRepository
import javax.inject.Inject

class GetVideosUseCase
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) {
        suspend operator fun invoke(): Outcome<List<Video>> = mediaRepository.getVideos()
    }
