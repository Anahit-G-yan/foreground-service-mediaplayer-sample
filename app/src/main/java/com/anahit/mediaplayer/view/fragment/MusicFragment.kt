package com.anahit.mediaplayer.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anahit.mediaplayer.R
import com.anahit.mediaplayer.view.adapter.adapter.MediaAdapter
import com.anahit.mediaplayer.model.MediaFileModel
import com.anahit.mediaplayer.view.activity.MediaActivity
import com.anahit.mediaplayer.viewmodel.media.MediaViewModel
import com.anahit.mediaplayer.viewmodel.music.MusicViewModel
import kotlin.collections.ArrayList

class MusicFragment : Fragment() {

    private val musicViewModel: MusicViewModel by viewModels()
    private val mediaViewModel: MediaViewModel by viewModels()

    private lateinit var musicsRecyclerView: RecyclerView
    private lateinit var serviceSwith: SwitchCompat
    private lateinit var switchText: TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserve()
        initClick()
        checkServiceTypeForSwitch()
    }


    private fun checkServiceTypeForSwitch() {
        if (musicViewModel.checkService()){
            serviceSwith.isChecked = true
            switchText.text = resources.getString(R.string.On)
        }else{
            serviceSwith.isChecked = false
            switchText.text = resources.getString(R.string.Off)
        }
    }


    private fun initClick() {
        serviceSwith.setOnClickListener {
            if (serviceSwith.isChecked){
                musicViewModel.saveForegroundService("foreground")
                switchText.text = resources.getString(R.string.On)
                Toast.makeText(requireContext(), resources.getString(R.string.Music_plays_in_the_foreground), Toast.LENGTH_SHORT).show()
            }else{
                switchText.text = resources.getString(R.string.Off)
                musicViewModel.removeForegroundService()
                Toast.makeText(requireContext(), resources.getString(R.string.Music_plays_in_the_background), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initView() {
        musicsRecyclerView = requireView().findViewById(R.id.musicsRecyclerView)
        serviceSwith = requireView().findViewById(R.id.serviceSwith)
        switchText = requireView().findViewById(R.id.switchText)

    }

    private fun initObserve() {
        mediaViewModel.getAllMusics()
        mediaViewModel.musicLiveData.observe(viewLifecycleOwner, {
            initMusicsAdapter(it)
        })
    }

    private fun initMusicsAdapter(music: ArrayList<MediaFileModel>) {
        val mediaAdapter = MediaAdapter(music)
        val linearLayoutManager = LinearLayoutManager(requireContext())

        mediaAdapter.musicClickListener { position, item ->

            val bundle = Bundle()
            bundle.putParcelable("musicFile", item)

            bundle.putParcelableArrayList("musicList", music)
            bundle.putInt("musicPosition", position)

            val detailedMusicFragment = DetailedMusicFragment()
            detailedMusicFragment.arguments = bundle
            val mediaActivity = activity as MediaActivity
            mediaActivity.openFragment(R.id.fragmentContainer, detailedMusicFragment)

        }
        musicsRecyclerView.layoutManager = linearLayoutManager
        musicsRecyclerView.adapter = mediaAdapter

    }

}