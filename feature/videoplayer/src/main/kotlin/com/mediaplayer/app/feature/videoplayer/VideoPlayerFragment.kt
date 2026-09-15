package com.mediaplayer.app.feature.videoplayer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mediaplayer.app.core.ui.NavDestinations
import com.mediaplayer.app.core.ui.viewBinding
import com.mediaplayer.app.feature.videoplayer.databinding.FragmentVideoPlayerBinding

/**
 * A single local ExoPlayer instance scoped to this screen - unlike music, video has no queue or
 * background/notification requirement, so it doesn't go through core:media's PlaybackService.
 *
 * The player is created once (in [onViewCreated]) and only paused/resumed in [onStop]/[onStart],
 * not torn down - so a brief backgrounding (e.g. a notification shade, multi-window resize)
 * doesn't reset playback position. It's fully released in [onDestroyView], once the underlying
 * [android.view.View] it's attached to is gone.
 */
class VideoPlayerFragment : Fragment(R.layout.fragment_video_player) {
    private val binding by viewBinding(FragmentVideoPlayerBinding::bind)
    private var player: ExoPlayer? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val exoPlayer =
            ExoPlayer.Builder(requireContext()).build().apply {
                setMediaItem(MediaItem.fromUri(videoPath()))
                addListener(
                    object : Player.Listener {
                        override fun onPlayerError(error: PlaybackException) {
                            Toast
                                .makeText(requireContext(), R.string.failed_to_play_video, Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                )
                prepare()
            }
        binding.playerView.player = exoPlayer
        player = exoPlayer
    }

    override fun onStart() {
        super.onStart()
        player?.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        player?.playWhenReady = false
    }

    override fun onDestroyView() {
        binding.playerView.player = null
        player?.release()
        player = null
        super.onDestroyView()
    }

    private fun videoPath(): String = requireArguments().getString(NavDestinations.ARG_VIDEO_PATH).orEmpty()
}
