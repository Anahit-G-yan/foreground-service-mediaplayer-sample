package com.mediaplayer.app.domain.usecase

import com.mediaplayer.app.domain.model.FavoriteTrack
import com.mediaplayer.app.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase
    @Inject
    constructor(
        private val favoritesRepository: FavoritesRepository,
    ) {
        operator fun invoke(): Flow<List<FavoriteTrack>> = favoritesRepository.observeFavorites()
    }
