package com.mediaplayer.app.feature.library.videolist

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
import com.mediaplayer.app.domain.model.Video
import com.mediaplayer.app.feature.library.R
import com.mediaplayer.app.feature.library.databinding.FragmentVideoListBinding
import com.mediaplayer.app.feature.library.list.MediaListAdapter
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
        state.renderListVisibility(
            loadingIndicator = binding.loadingIndicator,
            content = binding.videoRecyclerView,
            placeholder = ListPlaceholder(binding.placeholderContainer, binding.messageText),
            emptyMessageRes = R.string.no_video_found,
            errorMessageRes = R.string.failed_to_load_video,
        )

        if (state is UiState.Success) {
            adapter.submitList(state.data)
        }
    }
}
