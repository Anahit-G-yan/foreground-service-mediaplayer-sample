package com.anahit.mediaplayer.feature.player

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class PlayerViewModel
    @Inject
    constructor(
        private val playerController: PlayerController,
        private val artworkProvider: ArtworkProvider,
        private val observeFavoritesUseCase: ObserveFavoritesUseCase,
        private val addFavoriteUseCase: AddFavoriteUseCase,
        private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    ) : ViewModel() {
        val playbackState: StateFlow<PlaybackState> = playerController.playbackState

        // Purely for the UI (favorite icon swap) - toggleFavorite() below does its own one-shot
        // check instead of reading this, so it's correct even before anything has subscribed here.
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
                    .collect { track -> _artwork.value = track?.let { artworkProvider.embeddedArtwork(it.path) } }
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
                val alreadyFavorited = observeFavoritesUseCase().first().any { it.id == track.id }
                if (alreadyFavorited) {
                    removeFavoriteUseCase(track.id)
                } else {
                    addFavoriteUseCase(
                        FavoriteTrack(
                            id = track.id,
                            title = track.title,
                            path = track.path,
                            durationMillis = track.durationMillis,
                            artist = track.artist,
                            artwork = artworkProvider.embeddedArtwork(track.path),
                        ),
                    )
                }
            }
        }
    }
