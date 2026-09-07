package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.domain.repository.FavoritesRepository
import javax.inject.Inject

class RemoveFavoriteUseCase
    @Inject
    constructor(
        private val favoritesRepository: FavoritesRepository,
    ) {
        suspend operator fun invoke(trackId: String) = favoritesRepository.removeFavorite(trackId)
    }
