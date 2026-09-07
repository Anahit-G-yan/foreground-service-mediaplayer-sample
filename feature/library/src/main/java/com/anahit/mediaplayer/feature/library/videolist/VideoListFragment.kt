package com.anahit.mediaplayer.feature.library.videolist

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
import com.anahit.mediaplayer.domain.model.Video
import com.anahit.mediaplayer.feature.library.R
import com.anahit.mediaplayer.feature.library.databinding.FragmentVideoListBinding
import com.anahit.mediaplayer.feature.library.list.MediaListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoListFragment : Fragment(R.layout.fragment_video_list) {
    private val viewModel: VideoListViewModel by viewModels()
    private val binding by viewBinding(FragmentVideoListBinding::bind)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val adapter =
            MediaListAdapter { item ->
                findNavController().navigate(NavDestinations.videoPlayer(item.path))
            }
        binding.videoRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.videoRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state, adapter) }
            }
        }
    }

    private fun render(
        state: UiState<List<Video>>,
        adapter: MediaListAdapter,
    ) {
        binding.loadingIndicator.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
        binding.videoRecyclerView.visibility = if (state is UiState.Success) View.VISIBLE else View.GONE
        binding.messageText.visibility =
            if (state is UiState.Empty || state is UiState.Error) View.VISIBLE else View.GONE

        when (state) {
            is UiState.Success -> adapter.submitList(state.data)
            is UiState.Empty -> binding.messageText.text = getString(R.string.no_video_found)
            is UiState.Error -> binding.messageText.text = getString(R.string.failed_to_load_video)
            is UiState.Loading -> Unit
        }
    }
}
