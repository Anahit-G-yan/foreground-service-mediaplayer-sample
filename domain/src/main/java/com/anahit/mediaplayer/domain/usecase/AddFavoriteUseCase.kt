package com.anahit.mediaplayer.domain.usecase

import com.anahit.mediaplayer.domain.model.FavoriteTrack
import com.anahit.mediaplayer.domain.repository.FavoritesRepository
import javax.inject.Inject

class AddFavoriteUseCase
    @Inject
    constructor(
        private val favoritesRepository: FavoritesRepository,
    ) {
        suspend operator fun invoke(track: FavoriteTrack) = favoritesRepository.addFavorite(track)
    }
