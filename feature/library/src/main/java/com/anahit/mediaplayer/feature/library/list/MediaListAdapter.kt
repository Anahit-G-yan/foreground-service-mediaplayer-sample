package com.anahit.mediaplayer.feature.library.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.anahit.mediaplayer.feature.library.databinding.ItemMediaBinding
import com.anahit.mediaplayer.domain.model.MediaItem as DomainMediaItem

class MediaListAdapter(
    private val onClick: (DomainMediaItem) -> Unit,
) : ListAdapter<DomainMediaItem, MediaItemViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MediaItemViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaItemViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(
        holder: MediaItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<DomainMediaItem>() {
        override fun areItemsTheSame(
            oldItem: DomainMediaItem,
            newItem: DomainMediaItem,
        ): Boolean = oldItem.id == newItem.id

        // Lint's DiffUtilEquals check only looks for equals() on the sealed interface itself;
        // both of its implementations (Track, Video) are data classes with a real equals().
        @Suppress("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: DomainMediaItem,
            newItem: DomainMediaItem,
        ): Boolean = oldItem == newItem
    }
}
