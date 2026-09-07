package com.mediaplayer.app.data.repository

import com.mediaplayer.app.core.database.dao.FavoriteDao
import com.mediaplayer.app.data.mapper.toDomain
import com.mediaplayer.app.data.mapper.toEntity
import com.mediaplayer.app.domain.model.FavoriteTrack
import com.mediaplayer.app.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl
    @Inject
    constructor(
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
