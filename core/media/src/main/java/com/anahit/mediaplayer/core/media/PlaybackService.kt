package com.anahit.mediaplayer.core.media

import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint

private const val SEEK_INCREMENT_MILLIS = 2_500L

/**
 * Replaces the old MusicService: no more manual startForeground/notification bookkeeping
 * (Media3's MediaSessionService + DefaultMediaNotificationProvider handle that), and no more
 * hand-rolled position polling in the UI layer (see [PlayerControllerImpl]). Playback always runs
 * as a foreground service with a notification - there's no background-only mode.
 */
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        val player =
            ExoPlayer
                .Builder(this)
                .setAudioAttributes(
                    AudioAttributes
                        .Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                    true,
                ).setHandleAudioBecomingNoisy(true)
                .setSeekBackIncrementMs(SEEK_INCREMENT_MILLIS)
                .setSeekForwardIncrementMs(SEEK_INCREMENT_MILLIS)
                .build()

        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    /**
     * Standard Media3 guidance: keep the foreground service (and its notification) alive only
     * while actually playing.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player ?: return
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
