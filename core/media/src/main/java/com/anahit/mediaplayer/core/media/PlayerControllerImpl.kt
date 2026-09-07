package com.anahit.mediaplayer.core.media

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.anahit.mediaplayer.core.media.mapper.toPlayerItem
import com.anahit.mediaplayer.domain.model.MediaItem
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val POSITION_UPDATE_INTERVAL_MILLIS = 500L

@Suppress("TooManyFunctions") // one cohesive responsibility: the full player-control surface
@Singleton
class PlayerControllerImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : PlayerController {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        private var controller: MediaController? = null
        private var positionTickerJob: Job? = null

        private val _playbackState = MutableStateFlow(PlaybackState())
        override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

        init {
            val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
            val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture.addListener(
                {
                    controller = controllerFuture.get().also(::attachListener)
                },
                MoreExecutors.directExecutor(),
            )
        }

        override fun setQueue(
            items: List<MediaItem>,
            startIndex: Int,
        ) {
            val player = controller ?: return
            val startPositionMillis = 0L
            player.setMediaItems(items.map { it.toPlayerItem() }, startIndex, startPositionMillis)
            player.prepare()
            player.play()
        }

        override fun playPause() {
            val player = controller ?: return
            if (player.isPlaying) player.pause() else player.play()
        }

        override fun seekTo(positionMillis: Long) {
            controller?.seekTo(positionMillis)
        }

        override fun seekForward() {
            controller?.seekForward()
        }

        override fun seekBack() {
            controller?.seekBack()
        }

        override fun skipToNext() {
            controller?.seekToNext()
        }

        override fun skipToPrevious() {
            controller?.seekToPrevious()
        }

        override fun setRepeatMode(mode: RepeatMode) {
            controller?.repeatMode = mode.toPlayerRepeatMode()
        }

        private fun attachListener(player: MediaController) {
            updateState(player)
            player.addListener(
                object : Player.Listener {
                    override fun onEvents(
                        player: Player,
                        events: Player.Events,
                    ) {
                        updateState(player)
                        managePositionTicker(player)
                    }
                },
            )
            managePositionTicker(player)
        }

        private fun managePositionTicker(player: Player) {
            if (player.isPlaying) {
                if (positionTickerJob?.isActive == true) return
                positionTickerJob =
                    scope.launch {
                        while (isActive) {
                            _playbackState.update { it.copy(positionMillis = player.currentPosition) }
                            delay(POSITION_UPDATE_INTERVAL_MILLIS)
                        }
                    }
            } else {
                positionTickerJob?.cancel()
                positionTickerJob = null
            }
        }

        private fun updateState(player: Player) {
            val mediaItem = player.currentMediaItem
            _playbackState.update {
                it.copy(
                    currentItemId = mediaItem?.mediaId,
                    currentItemTitle =
                        mediaItem
                            ?.mediaMetadata
                            ?.title
                            ?.toString()
                            .orEmpty(),
                    currentItemSubtitle = mediaItem?.mediaMetadata?.artist?.toString(),
                    isPlaying = player.isPlaying,
                    positionMillis = player.currentPosition,
                    durationMillis = player.duration.coerceAtLeast(0L),
                    hasNext = player.hasNextMediaItem(),
                    hasPrevious = player.hasPreviousMediaItem(),
                    repeatMode = player.repeatMode.toDomainRepeatMode(),
                )
            }
        }
    }

internal fun RepeatMode.toPlayerRepeatMode(): Int =
    when (this) {
        RepeatMode.OFF -> Player.REPEAT_MODE_OFF
        RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        RepeatMode.ALL -> Player.REPEAT_MODE_ALL
    }

internal fun Int.toDomainRepeatMode(): RepeatMode =
    when (this) {
        Player.REPEAT_MODE_ONE -> RepeatMode.ONE
        Player.REPEAT_MODE_ALL -> RepeatMode.ALL
        else -> RepeatMode.OFF
    }
