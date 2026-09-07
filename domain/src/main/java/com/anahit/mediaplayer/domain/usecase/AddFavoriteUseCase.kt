package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.domain.model.FavoriteTrack
import com.anahit.mediaplayer.domain.repository.FavoritesRepository

class AddFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun invoke(track: FavoriteTrack) = favoritesRepository.addFavorite(track)
}
