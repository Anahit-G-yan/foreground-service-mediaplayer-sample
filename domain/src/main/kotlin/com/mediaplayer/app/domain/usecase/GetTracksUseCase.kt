package com.mediaplayer.app.domain.usecase

import com.mediaplayer.app.core.common.Outcome
import com.mediaplayer.app.domain.model.Track
import com.mediaplayer.app.domain.repository.MediaRepository
import javax.inject.Inject

class GetTracksUseCase
    @Inject
    constructor(
        private val mediaRepository: MediaRepository,
    ) {
        suspend operator fun invoke(): Outcome<List<Track>> = mediaRepository.getTracks()
    }
