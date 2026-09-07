package com.anahit.mediaplayer.viewmodel.media

import android.app.Application
import android.content.ContentResolver
import android.provider.MediaStore
import com.anahit.mediaplayer.model.MediaFileModel


class ContentProviderHandler(
    private var mContext: Application,
) {

    fun getAllMusics(completion: (ArrayList<MediaFileModel>?) -> Unit) {
        Thread {
            try {
                val result = getAllMusics()
                completion.invoke(result)
            } catch (error: Exception) {
                completion.invoke(null)
            }
        }.start()
    }

    fun getAllVideos(completion: (ArrayList<MediaFileModel>?) -> Unit){
        Thread {
            try {
                val result = getAllVideos()
                completion.invoke(result)
            } catch (error: Exception) {
                completion.invoke(null)
            }
        }.start()
    }

    private fun getAllMusics(): ArrayList<MediaFileModel> {
        val result = ArrayList<MediaFileModel>()

        val contentResolver: ContentResolver = mContext.contentResolver

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val queryProjection = arrayOf<String>(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
        )

        val queryOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        contentResolver.query(uri, queryProjection, null, null, queryOrder).use { cursor ->
            if (cursor == null) return@use
            val columnId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val columnTitle = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val columnArtist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val columnPath = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val columnDuration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getString(columnId)
                val title = cursor.getString(columnTitle)
                val artist = cursor.getString(columnArtist)
                val path = cursor.getString(columnPath)
                val duration = cursor.getString(columnDuration)

                //for taking only musics, no audio recordings
                val fileMP3 = path.endsWith(".mp3")
                val noNotificationVoice = duration.toInt() > 10000
                if (fileMP3 && noNotificationVoice) {
                    val musicFileModel = MediaFileModel(
                        id = id,
                        title = title,
                        path = path,
                        duration = duration,
                        artist = artist,
                    )
                    result.add(musicFileModel)
                }
            }
        }
        return result
    }


    private fun getAllVideos(): ArrayList<MediaFileModel> {
        val result = ArrayList<MediaFileModel>()

        val contentResolver: ContentResolver = mContext.contentResolver

        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val queryProjection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
        )

        val queryOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        contentResolver.query(uri, queryProjection, null, null, queryOrder).use { cursor ->
            if (cursor == null) return@use
                val columnId = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val columnTitle = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                val columnPath = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val columnDuration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getString(columnId)
                    val title = cursor.getString(columnTitle)
                    val path = cursor.getString(columnPath)
                    val duration = cursor.getString(columnDuration)

                    val videoFileModel = MediaFileModel(
                        id = id,
                        title = title,
                        path = path,
                        duration = duration
                    )
                    result.add(videoFileModel)
                }
        }
        return result
    }
}

