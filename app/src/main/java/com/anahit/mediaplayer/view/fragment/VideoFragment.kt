package com.anahit.mediaplayer.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anahit.mediaplayer.R
import com.anahit.mediaplayer.view.adapter.adapter.MediaAdapter
import com.anahit.mediaplayer.model.MediaFileModel
import com.anahit.mediaplayer.view.activity.MediaActivity
import com.anahit.mediaplayer.viewmodel.media.MediaViewModel


class VideoFragment : Fragment() {

    private val mediaViewModel: MediaViewModel by viewModels()

    private lateinit var videosRecyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserve()
    }


    private fun initView() {
        videosRecyclerView = requireView().findViewById(R.id.videoRecyclerView)
    }

    private fun initObserve() {
        mediaViewModel.getAllVideos()
        mediaViewModel.videoLiveData.observe(viewLifecycleOwner, Observer {
            initMusicsAdapter(it)
        })
    }

    private fun initMusicsAdapter(video: ArrayList<MediaFileModel>) {
        val mediaAdapter = MediaAdapter(video)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        mediaAdapter.musicClickListener { position, item ->
            val bundle = Bundle()
            bundle.putParcelable("videoFile", item)
            bundle.putParcelableArrayList("videoList", video)
            bundle.putInt("videoPosition", position)
            val detailedVideoFragment = DetailedVideoFragment()
            detailedVideoFragment.arguments = bundle
            val mediaActivity = activity as MediaActivity
            mediaActivity.openFragment(R.id.fragmentContainer, detailedVideoFragment)
        }
        videosRecyclerView.layoutManager = linearLayoutManager
        videosRecyclerView.adapter = mediaAdapter

    }
}
