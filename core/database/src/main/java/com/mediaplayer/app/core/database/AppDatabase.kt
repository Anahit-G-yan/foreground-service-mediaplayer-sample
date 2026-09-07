package com.mediaplayer.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mediaplayer.app.core.database.dao.FavoriteDao
import com.mediaplayer.app.core.database.entity.FavoriteEntity

@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val DATABASE_NAME = "media_player.db"
    }
}
