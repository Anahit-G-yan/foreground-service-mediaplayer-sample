package com.anahit.mediaplayer.data.mapper

import com.anahit.mediaplayer.core.database.entity.FavoriteEntity
import com.anahit.mediaplayer.domain.model.FavoriteTrack

fun FavoriteEntity.toDomain(): FavoriteTrack =
    FavoriteTrack(
        id = trackId,
        title = title,
        path = path,
        durationMillis = durationMillis,
        artist = artist,
        artwork = artwork,
    )

fun FavoriteTrack.toEntity(): FavoriteEntity =
    FavoriteEntity(
        trackId = id,
        title = title,
        path = path,
        durationMillis = durationMillis,
        artist = artist,
        artwork = artwork,
    )
