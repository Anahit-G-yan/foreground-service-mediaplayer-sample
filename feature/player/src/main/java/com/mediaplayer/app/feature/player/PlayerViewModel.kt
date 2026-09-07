package com.mediaplayer.app.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediaplayer.app.core.media.PlaybackState
import com.mediaplayer.app.core.media.PlayerController
import com.mediaplayer.app.core.media.RepeatMode
import com.mediaplayer.app.domain.model.FavoriteTrack
import com.mediaplayer.app.domain.model.Track
import com.mediaplayer.app.domain.usecase.AddFavoriteUseCase
import com.mediaplayer.app.domain.usecase.ObserveFavoritesUseCase
import com.mediaplayer.app.domain.usecase.RemoveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

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

        // Only one screen ever collects this (PlayerFragment, inside repeatOnLifecycle), so there's
        // no point sharing it as a StateFlow - a plain Flow recomputed from the already-hot
        // playbackState and the Room-backed favorites flow is simpler and just as correct.
        // toggleFavorite() below does its own one-shot check instead of reading this.
        val isCurrentTrackFavorite: Flow<Boolean> =
            combine(playbackState, observeFavoritesUseCase()) { state, favorites ->
                val id = state.currentItem?.id
                id != null && favorites.any { it.id == id }
            }

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
