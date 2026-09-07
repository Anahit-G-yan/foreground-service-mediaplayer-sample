package com.mediaplayer.app.domain.usecase

import com.mediaplayer.app.domain.model.FavoriteTrack
import com.mediaplayer.app.domain.repository.FavoritesRepository
import javax.inject.Inject

class AddFavoriteUseCase
    @Inject
    constructor(
        private val favoritesRepository: FavoritesRepository,
    ) {
        suspend operator fun invoke(track: FavoriteTrack) = favoritesRepository.addFavorite(track)
    }
