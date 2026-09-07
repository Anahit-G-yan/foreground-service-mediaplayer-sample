package com.mediaplayer.app.domain.usecase

import com.mediaplayer.app.domain.repository.FavoritesRepository
import javax.inject.Inject

class RemoveFavoriteUseCase
    @Inject
    constructor(
        private val favoritesRepository: FavoritesRepository,
    ) {
        suspend operator fun invoke(trackId: String) = favoritesRepository.removeFavorite(trackId)
    }
