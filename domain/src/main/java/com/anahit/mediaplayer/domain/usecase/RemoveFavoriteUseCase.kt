package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.domain.repository.FavoritesRepository

class RemoveFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(trackId: String) = favoritesRepository.removeFavorite(trackId)
}
