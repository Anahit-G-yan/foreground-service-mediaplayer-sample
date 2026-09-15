package com.mediaplayer.app.feature.library.musiclist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mediaplayer.app.core.ui.ListPlaceholder
import com.mediaplayer.app.core.ui.NavDestinations
import com.mediaplayer.app.core.ui.UiState
import com.mediaplayer.app.core.ui.renderListVisibility
import com.mediaplayer.app.core.ui.viewBinding
import com.mediaplayer.app.domain.model.Track
import com.mediaplayer.app.feature.library.R
import com.mediaplayer.app.feature.library.databinding.FragmentMusicListBinding
import com.mediaplayer.app.feature.library.list.MediaListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicListFragment : Fragment(R.layout.fragment_music_list) {
    private val viewModel: MusicListViewModel by viewModels()
    private val binding by viewBinding(FragmentMusicListBinding::bind)
    private var tracks: List<Track> = emptyList()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val adapter =
            MediaListAdapter { item ->
                val index = tracks.indexOfFirst { it.id == item.id }
                if (index != -1) {
                    viewModel.play(tracks, index)
                    findNavController().navigate(NavDestinations.player)
                }
            }
        binding.musicsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.musicsRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state, adapter) }
            }
        }
    }

    private fun render(
        state: UiState<List<Track>>,
        adapter: MediaListAdapter,
    ) {
        state.renderListVisibility(
            loadingIndicator = binding.loadingIndicator,
            content = binding.musicsRecyclerView,
            placeholder = ListPlaceholder(binding.placeholderContainer, binding.messageText),
            emptyMessageRes = R.string.no_music_found,
            errorMessageRes = R.string.failed_to_load_music,
        )

        if (state is UiState.Success) {
            tracks = state.data
            adapter.submitList(state.data)
        }
    }
}
