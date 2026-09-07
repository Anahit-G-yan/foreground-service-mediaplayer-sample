package com.anahit.mediaplayer.data.repository

import android.content.Context
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.anahit.mediaplayer.core.common.DispatcherProvider
import com.anahit.mediaplayer.core.common.Outcome
import com.anahit.mediaplayer.core.common.runAsOutcome
import com.anahit.mediaplayer.domain.model.Track
import com.anahit.mediaplayer.domain.model.Video
import com.anahit.mediaplayer.domain.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Voice memos and notification blips also show up as ".mp3" files under the audio collection;
 * anything shorter than this is filtered out so the library only lists real tracks.
 */
private const val MIN_TRACK_DURATION_MILLIS = 10_000L
private const val MP3_EXTENSION = ".mp3"

internal fun isEligibleTrack(
    path: String,
    durationMillis: Long,
): Boolean = path.endsWith(MP3_EXTENSION, ignoreCase = true) && durationMillis > MIN_TRACK_DURATION_MILLIS

class MediaStoreRepositoryImpl
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        private val dispatcherProvider: DispatcherProvider,
    ) : MediaRepository {
        override suspend fun getTracks(): Outcome<List<Track>> =
            withContext(dispatcherProvider.io) {
                runAsOutcome { queryTracks() }
            }

        override suspend fun getVideos(): Outcome<List<Video>> =
            withContext(dispatcherProvider.io) {
                runAsOutcome { queryVideos() }
            }

        private fun queryTracks(): List<Track> {
            val projection =
                arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION,
                )
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            val tracks = mutableListOf<Track>()
            context.contentResolver
                .query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder,
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                    while (cursor.moveToNext()) {
                        val path = cursor.getStringOrNull(pathColumn)
                        val durationMillis = cursor.getLongOrNull(durationColumn) ?: 0L
                        if (path == null || !isEligibleTrack(path, durationMillis)) continue

                        tracks +=
                            Track(
                                id = cursor.getStringOrNull(idColumn).orEmpty(),
                                title = cursor.getStringOrNull(titleColumn).orEmpty(),
                                path = path,
                                durationMillis = durationMillis,
                                artist = cursor.getStringOrNull(artistColumn),
                            )
                    }
                }
            return tracks
        }

        private fun queryVideos(): List<Video> {
            val projection =
                arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DURATION,
                )
            val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

            val videos = mutableListOf<Video>()
            context.contentResolver
                .query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder,
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                    val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

                    while (cursor.moveToNext()) {
                        val path = cursor.getStringOrNull(pathColumn) ?: continue
                        videos +=
                            Video(
                                id = cursor.getStringOrNull(idColumn).orEmpty(),
                                title = cursor.getStringOrNull(titleColumn).orEmpty(),
                                path = path,
                                durationMillis = cursor.getLongOrNull(durationColumn) ?: 0L,
                            )
                    }
                }
            return videos
        }
    }
