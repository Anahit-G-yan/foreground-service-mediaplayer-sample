package com.anahit.mediaplayer.view.adapter.viewholder


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.anahit.mediaplayer.R
import com.anahit.mediaplayer.extension.convertingIntoMinutesAndSeconds
import com.anahit.mediaplayer.extension.thumbnailToByteArray
import com.anahit.mediaplayer.model.MediaFileModel

//TODO: please add comments
class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val mediaTitleIV = itemView.findViewById<TextView>(R.id.mediaTitle)
    private val mediaArtistIV = itemView.findViewById<TextView>(R.id.mediaArtist)
    private val mediaDurationIV = itemView.findViewById<TextView>(R.id.duration)
    private val mediaThumbnailIV = itemView.findViewById<ImageView>(R.id.mediaThumbnail)

    private lateinit var modelMedia: MediaFileModel

    fun onBind(modelMedia: MediaFileModel, musicClickListener: ((position: Int, item: MediaFileModel) -> Unit)?) {
        this.modelMedia = modelMedia
        initItemView()
        initOnClicks(musicClickListener)
    }


    /**
     *  On click listeners
     */
    private fun initOnClicks(musicClickListener: ((position: Int, item: MediaFileModel) -> Unit)?) {
        // listening item click
        itemView.setOnClickListener {
            musicClickListener?.invoke(position, modelMedia)
        }
    }


    private fun initItemView() {

        mediaTitleIV.text = modelMedia.title
        mediaArtistIV.text = modelMedia.artist
        mediaDurationIV.text = modelMedia.duration.convertingIntoMinutesAndSeconds()

        if (isMusic(modelMedia.path)){
            handleMusic()
        } else{
            handleVideo()
        }
    }


    private fun handleVideo() {
        Glide
            .with(mediaThumbnailIV.context)
            .load(modelMedia.path)
            .into(mediaThumbnailIV)
    }

    private fun handleMusic() {
        //for music IMAGE and THUMBNAIL
        val image = modelMedia.path.thumbnailToByteArray()
        //for checking, if there is a music system thumbnail
        if (image != null) {
            Glide
                .with(itemView.context)
                .asBitmap()
                .load(image)
                .into(mediaThumbnailIV)
        } else {
            //without music system thumbnail, we attach default image
            mediaThumbnailIV.setImageResource(R.drawable.music)
        }
    }

    private fun isMusic(filePath: String): Boolean{
        return filePath.endsWith(".mp3")
    }
}