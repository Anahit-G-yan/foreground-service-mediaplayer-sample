package com.anahit.mediaplayer.core.media

import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.anahit.mediaplayer.domain.repository.PlaybackSettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEEK_INCREMENT_MILLIS = 2_500L

/**
 * Replaces the old MusicService: no more manual startForeground/notification bookkeeping
 * (Media3's MediaSessionService + DefaultMediaNotificationProvider handle that), and no more
 * hand-rolled position polling in the UI layer (see [PlayerControllerImpl]).
 */
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    @Inject
    lateinit var playbackSettingsRepository: PlaybackSettingsRepository

    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

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

    override fun onTaskRemoved(rootIntent: Intent?) {
        val session = mediaSession ?: return
        serviceScope.launch {
            val keepPlayingInBackground = playbackSettingsRepository.observeForegroundPlaybackEnabled().first()
            if (!keepPlayingInBackground) {
                session.player.pause()
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
