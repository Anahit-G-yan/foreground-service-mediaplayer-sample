package com.anahit.mediaplayer.data.repository

import com.anahit.mediaplayer.core.database.dao.FavoriteDao
import com.anahit.mediaplayer.data.mapper.toDomain
import com.anahit.mediaplayer.data.mapper.toEntity
import com.anahit.mediaplayer.domain.model.FavoriteTrack
import com.anahit.mediaplayer.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val favoriteDao: FavoriteDao,
) : FavoritesRepository {
    override fun observeFavorites(): Flow<List<FavoriteTrack>> =
        favoriteDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun addFavorite(track: FavoriteTrack) {
        favoriteDao.insert(track.toEntity())
    }

    override suspend fun removeFavorite(trackId: String) {
        favoriteDao.deleteByTrackId(trackId)
    }
}
