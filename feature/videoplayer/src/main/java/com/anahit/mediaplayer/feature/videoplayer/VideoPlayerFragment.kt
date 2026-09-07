package com.anahit.mediaplayer.feature.videoplayer

import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.anahit.mediaplayer.core.ui.NavDestinations
import com.anahit.mediaplayer.core.ui.viewBinding
import com.anahit.mediaplayer.feature.videoplayer.databinding.FragmentVideoPlayerBinding

/**
 * A single local ExoPlayer instance scoped to this screen - unlike music, video has no queue or
 * background/notification requirement, so it doesn't go through core:media's PlaybackService.
 */
class VideoPlayerFragment : Fragment(R.layout.fragment_video_player) {
    private val binding by viewBinding(FragmentVideoPlayerBinding::bind)
    private var player: ExoPlayer? = null

    override fun onStart() {
        super.onStart()
        val exoPlayer = ExoPlayer.Builder(requireContext()).build()
        exoPlayer.setMediaItem(MediaItem.fromUri(videoPath()))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        binding.playerView.player = exoPlayer
        player = exoPlayer
    }

    override fun onStop() {
        super.onStop()
        binding.playerView.player = null
        player?.release()
        player = null
    }

    private fun videoPath(): String = requireArguments().getString(NavDestinations.ARG_VIDEO_PATH).orEmpty()
}
