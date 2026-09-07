package com.anahit.mediaplayer.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.anahit.mediaplayer.room.entity.MediaEntity


@Dao
interface MediaDao {

    @Insert
    fun insert(mediaEntity: MediaEntity)

    @Query("select * from media")
    fun getAll(): List<MediaEntity>

    @Query("delete from media where mediaId = :mediaId")
    fun deleteMediaById(mediaId: String)

}