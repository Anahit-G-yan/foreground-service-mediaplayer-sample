package com.anahit.mediaplayer.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Not a data class: [artwork] is a [ByteArray], whose structural equals/hashCode would be
 * misleading (identity-based content comparison, not what callers expect).
 */
@Entity(
    tableName = "favorites",
    indices = [Index(value = ["trackId"], unique = true)],
)
class FavoriteEntity(
    val trackId: String,
    val title: String,
    val path: String,
    val durationMillis: Long,
    val artist: String?,
    val artwork: ByteArray?,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
