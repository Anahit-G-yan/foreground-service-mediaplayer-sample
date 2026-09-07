package com.mediaplayer.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mediaplayer.app.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorites ORDER BY title ASC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    suspend fun deleteByTrackId(trackId: String)
}
