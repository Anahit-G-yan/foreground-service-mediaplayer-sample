package com.mediaplayer.app.feature.library.videolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediaplayer.app.core.common.Outcome
import com.mediaplayer.app.core.ui.UiState
import com.mediaplayer.app.domain.model.Video
import com.mediaplayer.app.domain.usecase.GetVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel
    @Inject
    constructor(
        private val getVideosUseCase: GetVideosUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<UiState<List<Video>>>(UiState.Loading)
        val uiState: StateFlow<UiState<List<Video>>> = _uiState.asStateFlow()

        init {
            loadVideos()
        }

        fun loadVideos() {
            viewModelScope.launch {
                _uiState.value = UiState.Loading
                _uiState.value =
                    when (val result = getVideosUseCase()) {
                        is Outcome.Success ->
                            result.data.takeIf { it.isNotEmpty() }?.let { UiState.Success(it) }
                                ?: UiState.Empty
                        is Outcome.Error -> UiState.Error(result.throwable)
                    }
            }
        }
    }
