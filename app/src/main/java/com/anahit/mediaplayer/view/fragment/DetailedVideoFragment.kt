package com.anahit.mediaplayer.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.anahit.mediaplayer.R
import com.anahit.mediaplayer.model.MediaFileModel


class DetailedVideoFragment : Fragment() {

    private lateinit var modelMedia: MediaFileModel
    private lateinit var videoView: VideoView


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detailed_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getArgumentData()
        initView()
        initUI()
    }


    private fun initUI() {
        videoView.setVideoPath(modelMedia.path)
        videoView.setMediaController(MediaController(requireContext()))
        videoView.start()
    }


    private fun initView() {
        videoView = requireView().findViewById(R.id.videoView)
    }

    private fun getArgumentData() {
        if (arguments == null) return
         modelMedia = requireArguments().getParcelable<MediaFileModel>("videoFile") as MediaFileModel
    }
}