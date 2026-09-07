package com.anahit.mediaplayer.feature.library.musiclist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anahit.mediaplayer.core.common.Outcome
import com.anahit.mediaplayer.core.media.PlayerController
import com.anahit.mediaplayer.core.ui.UiState
import com.anahit.mediaplayer.domain.model.Track
import com.anahit.mediaplayer.domain.usecase.GetTracksUseCase
import com.anahit.mediaplayer.domain.usecase.ObserveForegroundPlaybackUseCase
import com.anahit.mediaplayer.domain.usecase.SetForegroundPlaybackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class MusicListViewModel
    @Inject
    constructor(
        private val getTracksUseCase: GetTracksUseCase,
        observeForegroundPlaybackUseCase: ObserveForegroundPlaybackUseCase,
        private val setForegroundPlaybackUseCase: SetForegroundPlaybackUseCase,
        private val playerController: PlayerController,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<UiState<List<Track>>>(UiState.Loading)
        val uiState: StateFlow<UiState<List<Track>>> = _uiState.asStateFlow()

        val foregroundPlaybackEnabled: StateFlow<Boolean> =
            observeForegroundPlaybackUseCase()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MILLIS), false)

        init {
            loadTracks()
        }

        fun loadTracks() {
            viewModelScope.launch {
                _uiState.value = UiState.Loading
                _uiState.value =
                    when (val result = getTracksUseCase()) {
                        is Outcome.Success ->
                            result.data.takeIf { it.isNotEmpty() }?.let { UiState.Success(it) }
                                ?: UiState.Empty
                        is Outcome.Error -> UiState.Error(result.throwable)
                    }
            }
        }

        fun setForegroundPlaybackEnabled(enabled: Boolean) {
            viewModelScope.launch { setForegroundPlaybackUseCase(enabled) }
        }

        fun play(
            tracks: List<Track>,
            startIndex: Int,
        ) {
            playerController.setQueue(tracks, startIndex)
        }
    }
