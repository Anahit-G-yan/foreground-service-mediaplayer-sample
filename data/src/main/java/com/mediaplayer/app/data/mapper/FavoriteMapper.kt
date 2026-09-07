package com.mediaplayer.app.data.mapper

import com.mediaplayer.app.core.database.entity.FavoriteEntity
import com.mediaplayer.app.domain.model.FavoriteTrack

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
