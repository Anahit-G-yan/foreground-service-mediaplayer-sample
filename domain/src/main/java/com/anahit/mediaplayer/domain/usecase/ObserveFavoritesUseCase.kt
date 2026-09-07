package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.domain.model.FavoriteTrack
import com.anahit.mediaplayer.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(): Flow<List<FavoriteTrack>> = favoritesRepository.observeFavorites()
}
