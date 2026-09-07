package com.anahit.mediaplayer.feature.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anahit.mediaplayer.core.media.PlaybackState
import com.anahit.mediaplayer.core.media.PlayerController
import com.anahit.mediaplayer.core.media.RepeatMode
import com.anahit.mediaplayer.domain.model.FavoriteTrack
import com.anahit.mediaplayer.domain.model.Track
import com.anahit.mediaplayer.domain.usecase.AddFavoriteUseCase
import com.anahit.mediaplayer.domain.usecase.ObserveFavoritesUseCase
import com.anahit.mediaplayer.domain.usecase.RemoveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import com.anahit.mediaplayer.core.ui.R as CoreUiR

private const val SUBSCRIPTION_TIMEOUT_MILLIS = 5_000L
private const val ARTWORK_QUALITY = 100

@HiltViewModel
class PlayerViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val playerController: PlayerController,
        observeFavoritesUseCase: ObserveFavoritesUseCase,
        private val addFavoriteUseCase: AddFavoriteUseCase,
        private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    ) : ViewModel() {
        val playbackState: StateFlow<PlaybackState> = playerController.playbackState

        val isCurrentTrackFavorite: StateFlow<Boolean> =
            combine(playbackState, observeFavoritesUseCase()) { state, favorites ->
                val id = state.currentItem?.id
                id != null && favorites.any { it.id == id }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MILLIS), false)

        private val _artwork = MutableStateFlow<ByteArray?>(null)

        /** The current track's embedded artwork, decoded off the main thread as the track changes. */
        val artwork: StateFlow<ByteArray?> = _artwork.asStateFlow()

        init {
            viewModelScope.launch {
                playbackState
                    .map { it.currentItem as? Track }
                    .distinctUntilChanged()
                    .collect { track ->
                        _artwork.value = track?.let { withContext(Dispatchers.IO) { extractEmbeddedArtwork(it.path) } }
                    }
            }
        }

        fun playPause() = playerController.playPause()

        fun seekTo(positionMillis: Long) = playerController.seekTo(positionMillis)

        fun seekForward() = playerController.seekForward()

        fun seekBack() = playerController.seekBack()

        fun skipToNext() = playerController.skipToNext()

        fun skipToPrevious() = playerController.skipToPrevious()

        fun toggleRepeatCurrentTrack() {
            val next = if (playbackState.value.repeatMode == RepeatMode.ONE) RepeatMode.OFF else RepeatMode.ONE
            playerController.setRepeatMode(next)
        }

        fun toggleFavorite() {
            val track = playbackState.value.currentItem as? Track ?: return
            viewModelScope.launch {
                if (isCurrentTrackFavorite.value) {
                    removeFavoriteUseCase(track.id)
                } else {
                    val artwork = withContext(Dispatchers.IO) { loadArtwork(track.path) }
                    addFavoriteUseCase(
                        FavoriteTrack(
                            id = track.id,
                            title = track.title,
                            path = track.path,
                            durationMillis = track.durationMillis,
                            artist = track.artist,
                            artwork = artwork,
                        ),
                    )
                }
            }
        }

        private fun loadArtwork(path: String): ByteArray {
            val embedded = extractEmbeddedArtwork(path)
            if (embedded != null) return embedded

            val bitmap = BitmapFactory.decodeResource(context.resources, CoreUiR.drawable.music)
            return ByteArrayOutputStream().use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, ARTWORK_QUALITY, stream)
                stream.toByteArray()
            }
        }

        // A broad catch is deliberate: MediaMetadataRetriever throws on unreadable/corrupt/DRM'd
        // files, and the correct response here is just "no artwork", not a crash.
        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        private fun extractEmbeddedArtwork(path: String): ByteArray? {
            val retriever = MediaMetadataRetriever()
            return try {
                retriever.setDataSource(path)
                retriever.embeddedPicture
            } catch (e: RuntimeException) {
                null
            } finally {
                retriever.release()
            }
        }
    }
