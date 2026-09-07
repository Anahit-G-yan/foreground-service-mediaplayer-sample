package com.anahit.mediaplayer.feature.library.list

import androidx.recyclerview.widget.RecyclerView
import com.anahit.mediaplayer.core.ui.formatAsMinutesAndSeconds
import com.anahit.mediaplayer.domain.model.Track
import com.anahit.mediaplayer.feature.library.databinding.ItemMediaBinding
import com.bumptech.glide.Glide
import com.anahit.mediaplayer.core.ui.R as CoreUiR
import com.anahit.mediaplayer.domain.model.MediaItem as DomainMediaItem

class MediaItemViewHolder(
    private val binding: ItemMediaBinding,
    private val onClick: (DomainMediaItem) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: DomainMediaItem) {
        binding.mediaTitle.text = item.title
        binding.mediaSubtitle.text = (item as? Track)?.artist
        binding.mediaDuration.text = item.durationMillis.formatAsMinutesAndSeconds()

        // Video thumbnails come straight from the frame; audio files have no visual frame to
        // decode, so they fall back to a placeholder rather than blocking onBind on a synchronous
        // MediaMetadataRetriever read for embedded album art.
        Glide
            .with(binding.mediaThumbnail)
            .load(item.path)
            .placeholder(CoreUiR.drawable.music)
            .error(CoreUiR.drawable.music)
            .into(binding.mediaThumbnail)

        binding.root.setOnClickListener { onClick(item) }
    }
}
