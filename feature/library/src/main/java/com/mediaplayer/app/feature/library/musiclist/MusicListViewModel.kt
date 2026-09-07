package com.mediaplayer.app.feature.library.musiclist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediaplayer.app.core.common.Outcome
import com.mediaplayer.app.core.media.PlayerController
import com.mediaplayer.app.core.ui.UiState
import com.mediaplayer.app.domain.model.Track
import com.mediaplayer.app.domain.usecase.GetTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel
    @Inject
    constructor(
        private val getTracksUseCase: GetTracksUseCase,
        private val playerController: PlayerController,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<UiState<List<Track>>>(UiState.Loading)
        val uiState: StateFlow<UiState<List<Track>>> = _uiState.asStateFlow()

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

        fun play(
            tracks: List<Track>,
            startIndex: Int,
        ) {
            playerController.setQueue(tracks, startIndex)
        }
    }
