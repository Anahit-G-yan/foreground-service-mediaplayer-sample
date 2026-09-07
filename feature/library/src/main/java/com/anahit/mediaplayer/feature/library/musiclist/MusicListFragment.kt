package com.anahit.mediaplayer.feature.library.musiclist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anahit.mediaplayer.core.ui.NavDestinations
import com.anahit.mediaplayer.core.ui.UiState
import com.anahit.mediaplayer.core.ui.viewBinding
import com.anahit.mediaplayer.domain.model.Track
import com.anahit.mediaplayer.feature.library.R
import com.anahit.mediaplayer.feature.library.databinding.FragmentMusicListBinding
import com.anahit.mediaplayer.feature.library.list.MediaListAdapter
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

        binding.foregroundSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setForegroundPlaybackEnabled(isChecked)
        }

        observeState(adapter)
    }

    private fun observeState(adapter: MediaListAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.uiState.collect { state -> render(state, adapter) } }
                launch {
                    viewModel.foregroundPlaybackEnabled.collect { enabled ->
                        binding.foregroundSwitch.isChecked = enabled
                        binding.switchText.text = getString(if (enabled) R.string.service_on else R.string.service_off)
                    }
                }
            }
        }
    }

    private fun render(
        state: UiState<List<Track>>,
        adapter: MediaListAdapter,
    ) {
        binding.loadingIndicator.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
        binding.musicsRecyclerView.visibility = if (state is UiState.Success) View.VISIBLE else View.GONE
        binding.messageText.visibility =
            if (state is UiState.Empty || state is UiState.Error) View.VISIBLE else View.GONE

        when (state) {
            is UiState.Success -> {
                tracks = state.data
                adapter.submitList(state.data)
            }
            is UiState.Empty -> binding.messageText.text = getString(R.string.no_music_found)
            is UiState.Error -> binding.messageText.text = getString(R.string.failed_to_load_music)
            is UiState.Loading -> Unit
        }
    }
}
