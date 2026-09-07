package com.anahit.mediaplayer.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "media",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
class MediaEntity(
    var mediaId: String,
    var title: String,
    var path: String,
    var duration: String,
    var byteArray: ByteArray? = null,
    var artist: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}