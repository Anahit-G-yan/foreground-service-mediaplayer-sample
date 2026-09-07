package com.anahit.mediaplayer.feature.player

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.anahit.mediaplayer.core.media.PlaybackState
import com.anahit.mediaplayer.core.media.RepeatMode
import com.anahit.mediaplayer.core.ui.formatAsMinutesAndSeconds
import com.anahit.mediaplayer.core.ui.viewBinding
import com.anahit.mediaplayer.domain.model.Track
import com.anahit.mediaplayer.feature.player.databinding.FragmentPlayerBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.anahit.mediaplayer.core.ui.R as CoreUiR

private const val HOLD_SEEK_REPEAT_INTERVAL_MILLIS = 400L

@AndroidEntryPoint
class PlayerFragment : Fragment(R.layout.fragment_player) {
    private val viewModel: PlayerViewModel by viewModels()
    private val binding by viewBinding(FragmentPlayerBinding::bind)
    private var isUserSeeking = false

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
        observeState()
    }

    private fun initClicks() {
        binding.playPauseButton.setOnClickListener { viewModel.playPause() }
        binding.nextButton.setOnClickListener { viewModel.skipToNext() }
        binding.prevButton.setOnClickListener { viewModel.skipToPrevious() }
        holdToRepeat(binding.nextButton) { viewModel.seekForward() }
        holdToRepeat(binding.prevButton) { viewModel.seekBack() }

        binding.repeatOffIcon.setOnClickListener { viewModel.toggleRepeatCurrentTrack() }
        binding.repeatOneIcon.setOnClickListener { viewModel.toggleRepeatCurrentTrack() }

        binding.favoriteIcon.setOnClickListener {
            viewModel.toggleFavorite()
            toast(R.string.music_added_to_favorites)
        }
        binding.favoritedIcon.setOnClickListener {
            viewModel.toggleFavorite()
            toast(R.string.music_removed_from_favorites)
        }

        binding.positionSeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (fromUser) viewModel.seekTo(progress.toLong())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    isUserSeeking = true
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    isUserSeeking = false
                }
            },
        )
    }

    /** Holding the button repeats [action] every [HOLD_SEEK_REPEAT_INTERVAL_MILLIS] until release. */
    private fun holdToRepeat(
        view: View,
        action: () -> Unit,
    ) {
        var repeatJob: Job? = null
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    repeatJob =
                        viewLifecycleOwner.lifecycleScope.launch {
                            while (isActive) {
                                action()
                                delay(HOLD_SEEK_REPEAT_INTERVAL_MILLIS)
                            }
                        }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> repeatJob?.cancel()
            }
            false
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.playbackState.collect(::renderPlaybackState) }
                launch { viewModel.artwork.collect(::renderArtwork) }
                launch {
                    viewModel.isCurrentTrackFavorite.collect { isFavorite ->
                        binding.favoriteIcon.visibility = if (isFavorite) View.GONE else View.VISIBLE
                        binding.favoritedIcon.visibility = if (isFavorite) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    private fun renderPlaybackState(state: PlaybackState) {
        val track = state.currentItem as? Track
        binding.titleText.text = track?.title
        binding.artistText.text = track?.artist

        binding.positionSeekBar.max = state.durationMillis.toInt()
        if (!isUserSeeking) {
            binding.positionSeekBar.progress = state.positionMillis.toInt()
        }
        binding.positionText.text = state.positionMillis.formatAsMinutesAndSeconds()
        binding.durationText.text = state.durationMillis.formatAsMinutesAndSeconds()

        binding.playPauseButton.setImageResource(if (state.isPlaying) CoreUiR.drawable.pause else CoreUiR.drawable.play)

        val repeatsCurrentTrack = state.repeatMode == RepeatMode.ONE
        binding.repeatOffIcon.visibility = if (repeatsCurrentTrack) View.GONE else View.VISIBLE
        binding.repeatOneIcon.visibility = if (repeatsCurrentTrack) View.VISIBLE else View.GONE
    }

    private fun renderArtwork(artwork: ByteArray?) {
        if (artwork != null) {
            Glide
                .with(binding.thumbnailImage)
                .asBitmap()
                .load(artwork)
                .into(binding.thumbnailImage)
            Glide
                .with(binding.backgroundImage)
                .asBitmap()
                .load(artwork)
                .into(binding.backgroundImage)
            binding.backgroundImage.setColorFilter(Color.parseColor("#8A000000"))
        } else {
            binding.thumbnailImage.setImageResource(CoreUiR.drawable.music)
            binding.backgroundImage.setImageResource(CoreUiR.drawable.background)
            binding.backgroundImage.setColorFilter(Color.parseColor("#6D000000"))
        }
    }

    private fun toast(messageRes: Int) {
        Toast.makeText(requireContext(), getString(messageRes), Toast.LENGTH_SHORT).show()
    }
}
