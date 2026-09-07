package com.anahit.mediaplayer.domain.repository

import com.anahit.mediaplayer.domain.model.FavoriteTrack
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<FavoriteTrack>>

    suspend fun addFavorite(track: FavoriteTrack)

    suspend fun removeFavorite(trackId: String)
}
