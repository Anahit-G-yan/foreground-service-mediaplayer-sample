package com.mediaplayer.app.domain.repository

import com.mediaplayer.app.domain.model.FavoriteTrack
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<FavoriteTrack>>

    suspend fun addFavorite(track: FavoriteTrack)

    suspend fun removeFavorite(trackId: String)
}
