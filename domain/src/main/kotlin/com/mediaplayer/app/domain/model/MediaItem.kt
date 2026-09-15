package com.mediaplayer.app.domain.model

sealed interface MediaItem {
    val id: String
    val title: String
    val path: String
    val durationMillis: Long
}

data class Track(
    override val id: String,
    override val title: String,
    override val path: String,
    override val durationMillis: Long,
    val artist: String?,
) : MediaItem

data class Video(
    override val id: String,
    override val title: String,
    override val path: String,
    override val durationMillis: Long,
) : MediaItem
