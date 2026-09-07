package com.mediaplayer.app.feature.player

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.mediaplayer.app.core.media.PlaybackState
import com.mediaplayer.app.core.media.RepeatMode
import com.mediaplayer.app.core.ui.applySystemBarsPadding
import com.mediaplayer.app.core.ui.formatAsMinutesAndSeconds
import com.mediaplayer.app.core.ui.viewBinding
import com.mediaplayer.app.domain.model.Track
import com.mediaplayer.app.feature.player.databinding.FragmentPlayerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.mediaplayer.app.core.ui.R as CoreUiR

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

        binding.favoriteIconContainer.applySystemBarsPadding(top = true)
        binding.controlsContainer.applySystemBarsPadding(bottom = true)

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
        binding.repeatOneIndicator.setOnClickListener { viewModel.toggleRepeatCurrentTrack() }

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

    /**
     * Holding the button repeats [action] every [HOLD_SEEK_REPEAT_INTERVAL_MILLIS] until release.
     * The first repeat waits out the system long-press timeout so a plain tap (released before
     * that) never fires [action] and only the view's own click listener runs. Once a hold has
     * actually started repeating, the release is consumed (`true`) so the view's default touch
     * handling doesn't also register it as a click and fire next/previous on top of the seek.
     */
    private fun holdToRepeat(
        view: View,
        action: () -> Unit,
    ) {
        var repeatJob: Job? = null
        var isHolding = false
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isHolding = false
                    repeatJob =
                        viewLifecycleOwner.lifecycleScope.launch {
                            delay(ViewConfiguration.getLongPressTimeout().toLong())
                            isHolding = true
                            while (isActive) {
                                action()
                                delay(HOLD_SEEK_REPEAT_INTERVAL_MILLIS)
                            }
                        }
                    false
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    repeatJob?.cancel()
                    isHolding
                }
                else -> false
            }
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
        binding.playPauseButton.contentDescription =
            getString(if (state.isPlaying) R.string.cd_pause else R.string.cd_play)

        val repeatsCurrentTrack = state.repeatMode == RepeatMode.ONE
        binding.repeatOffIcon.visibility = if (repeatsCurrentTrack) View.GONE else View.VISIBLE
        binding.repeatOneIndicator.visibility = if (repeatsCurrentTrack) View.VISIBLE else View.GONE
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
            binding.backgroundImage.setColorFilter(
                ContextCompat.getColor(requireContext(), CoreUiR.color.imageColor),
            )
        } else {
            binding.thumbnailImage.setImageResource(CoreUiR.drawable.music)
            binding.backgroundImage.setImageResource(CoreUiR.drawable.background)
            binding.backgroundImage.setColorFilter(
                ContextCompat.getColor(requireContext(), CoreUiR.color.transparent),
            )
        }
    }

    private fun toast(messageRes: Int) {
        Toast.makeText(requireContext(), getString(messageRes), Toast.LENGTH_SHORT).show()
    }
}
