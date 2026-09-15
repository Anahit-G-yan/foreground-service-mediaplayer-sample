package com.mediaplayer.app.domain.model

/**
 * A [Track] the user has bookmarked, together with the artwork snapshot captured at the time
 * it was favorited. Not a data class: [artwork] is a [ByteArray], whose structural equals/hashCode
 * would be misleading (identity-based content comparison, not what callers expect).
 */
class FavoriteTrack(
    val id: String,
    val title: String,
    val path: String,
    val durationMillis: Long,
    val artist: String?,
    val artwork: ByteArray?,
)
