package com.anahit.mediaplayer.room

import android.content.Context
import androidx.room.*
import com.anahit.mediaplayer.room.dao.MediaDao
import com.anahit.mediaplayer.room.entity.MediaEntity

@Database(entities = [MediaEntity::class], version = 1)
abstract class MediaDatabase: RoomDatabase() {

    abstract fun mediaDao(): MediaDao

    companion object {
        private var mInstance: MediaDatabase? = null

        fun getInstance(context: Context): MediaDatabase {
            if (mInstance != null) {
                return mInstance!!;
            }
            synchronized(this) {
                mInstance = Room.databaseBuilder(
                        context, MediaDatabase::class.java,
                        "testRoomDbName"
                ).build()
            }
            return mInstance!!
        }
    }

}