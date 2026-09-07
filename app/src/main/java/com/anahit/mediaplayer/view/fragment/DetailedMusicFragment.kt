package com.anahit.mediaplayer.view.fragment

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.anahit.mediaplayer.R
import com.anahit.mediaplayer.extension.*
import com.anahit.mediaplayer.model.MediaFileModel
import com.anahit.mediaplayer.view.activity.MediaActivity
import com.anahit.mediaplayer.viewmodel.media.MediaViewModel
import com.anahit.mediaplayer.viewmodel.music.MusicViewModel


class DetailedMusicFragment : Fragment() {


    private val mediaViewModel: MediaViewModel by viewModels()
    private val musicViewModel: MusicViewModel by viewModels()

    private lateinit var detailedMusicTitle: TextView
    private lateinit var detailedMusicArtist: TextView
    private lateinit var detailedMusicDuration: TextView
    private lateinit var musicCurrentPosition: TextView
    private lateinit var detailedMusicImage: ImageView
    private lateinit var detailedMusicThumbnail: ImageView
    private lateinit var favoriteIcon: ImageView
    private lateinit var heartIcon: ImageView
    private lateinit var pauseAndPlayButton: ImageView
    private lateinit var nextMusicButton: ImageView
    private lateinit var prevMusicButton: ImageView
    private lateinit var loop: ImageView
    private lateinit var nextMusicArrow: ImageView
    private lateinit var musicSeekBar: SeekBar

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detailed_music, container, false)
    }

    private fun provideMusicTimeHandler() = (requireActivity() as MediaActivity).musicTimerHandler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgumentData()
        initView()
        updateUi()
        runService(musicViewModel.currentMedia())
        initClick()

        // Initializing live data observers
        initObservers()

        // starting check buttons pressed functionality
        musicViewModel.checkingButtonsPressed()
    }

    private fun drawMusicTimeInfo(musicCurrentTime: Int){
        val isPlaying = musicCurrentTime.currentPositionIntoSeconds()
        val duration = musicCurrentTime.toString().convertingIntoMinutesAndSeconds()
        musicSeekBar.max = musicViewModel.currentMedia().duration.durationToInt()
        musicSeekBar.progress = isPlaying
        musicCurrentPosition.text = duration
    }
    private fun handleTypeOfNextMusic(){
        if (loop.isVisible) {
            runService(musicViewModel.currentMedia())
        } else {
            musicViewModel.playNext()
        }
    }

    private fun handleButtonsPressed(){
        if (prevMusicButton.isPressed) {
            val mediaActivity = requireActivity() as MediaActivity
            mediaActivity.mMusicService.rewindMusic()
        }
        if (nextMusicButton.isPressed) {
            val mediaActivity = requireActivity() as MediaActivity
            mediaActivity.mMusicService.forwardMusic()
        }
    }


    private fun initObservers() {
        // Listening music action live data. callback is called, when user click next buttons
        musicViewModel.playNextLiveData.observe(viewLifecycleOwner) {
            runService(it)
            updateUi()
        }

        // Listening music action live data. callback is called, when user click prev buttons
        musicViewModel.playPrevLiveData.observe(viewLifecycleOwner) {
            runService(it)
            updateUi()
        }

        // observing buttons pressed live data
        musicViewModel.buttonsPressedLiveData.observe(viewLifecycleOwner) {
            handleButtonsPressed()
        }

        // observing type of next music live data
        provideMusicTimeHandler().typeOfNextMusicLiveData().observe(viewLifecycleOwner){
            handleTypeOfNextMusic()
        }

        provideMusicTimeHandler().currentMusicTimeLiveData().observe(viewLifecycleOwner){
            drawMusicTimeInfo(it)
        }
    }



    private fun runService(model: MediaFileModel) {
        if (musicViewModel.checkService()){
            val mediaActivity = activity as MediaActivity
            mediaActivity.mMusicService.playForegroundMusic(model)
        }else{
            val musicService = activity as MediaActivity
            musicService.mMusicService.playMusic(model)

        }
    }


    private fun initView() {
        detailedMusicTitle = requireView().findViewById(R.id.detailedMusicTitle)
        detailedMusicArtist = requireView().findViewById(R.id.detailedMusicArtist)
        detailedMusicDuration = requireView().findViewById(R.id.detailedMusicDuration)
        musicCurrentPosition = requireView().findViewById(R.id.musicDuration)
        detailedMusicImage = requireView().findViewById(R.id.detailedMusicImage)
        detailedMusicThumbnail = requireView().findViewById(R.id.detailedMusicThumbnail)
        favoriteIcon = requireView().findViewById(R.id.favorite)
        heartIcon = requireView().findViewById(R.id.heart)
        pauseAndPlayButton = requireView().findViewById(R.id.pauseAndPlayMusic)
        prevMusicButton = requireView().findViewById(R.id.prevMusic)
        nextMusicButton = requireView().findViewById(R.id.nextMusic)
        loop = requireView().findViewById(R.id.loop)
        nextMusicArrow = requireView().findViewById(R.id.nextMusicArrow)
        musicSeekBar = requireView().findViewById(R.id.musicSeekBar)
    }

    private fun getArgumentData() {
        if (arguments == null) return
        val modelMedia = requireArguments().getParcelable<MediaFileModel>("musicFile") as MediaFileModel
        val songList = requireArguments().getParcelableArrayList<MediaFileModel>("musicList") as ArrayList<MediaFileModel>
        val position = requireArguments().getInt("musicPosition")
        /////////////////////////////////////////////////////////////
        musicViewModel.addMediaData(modelMedia, songList, position)
    }

    private fun updateUi() {
        detailedMusicTitle.text = musicViewModel.currentMedia().title
        detailedMusicArtist.text = musicViewModel.currentMedia().artist
        //Music duration
        detailedMusicDuration.text = musicViewModel.currentMedia().duration.convertingIntoMinutesAndSeconds()
        //Music background image and thumbnail
        val image = musicViewModel.currentMedia().path.thumbnailToByteArray()
        if (image != null) {        //if there is a system thumbnail
            Glide.with(requireContext()).asBitmap().load(image).into(detailedMusicImage)
            detailedMusicImage.setColorFilter(Color.parseColor("#8A000000"))
            Glide.with(requireContext()).asBitmap().load(image).into(detailedMusicThumbnail)
        } else {
            detailedMusicImage.setImageResource(R.drawable.background)
            detailedMusicImage.setColorFilter(Color.parseColor("#6D000000"))
            detailedMusicThumbnail.scaleType = ImageView.ScaleType.FIT_XY
            detailedMusicThumbnail.setImageResource(R.drawable.music)
        }
    }


    private fun initClick() {

        musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val mediaActivity = requireActivity() as MediaActivity
                    mediaActivity.mMusicService.progressChanged(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        loop.setOnClickListener {
            loop.visibility = View.GONE
            nextMusicArrow.visibility = View.VISIBLE

        }

        nextMusicArrow.setOnClickListener {
            nextMusicArrow.visibility = View.GONE
            loop.visibility = View.VISIBLE
        }

        pauseAndPlayButton.setOnClickListener {
            val mediaActivity = requireActivity() as MediaActivity
            if (mediaActivity.mMusicService.isPlaying()) {
                mediaActivity.mMusicService.pause()
                pauseAndPlayButton.setImageResource(R.drawable.play)
            } else {
                mediaActivity.mMusicService.play()
                pauseAndPlayButton.setImageResource(R.drawable.pause)
            }
        }



        nextMusicButton.setOnClickListener {

            musicViewModel.playNext()
        }

        prevMusicButton.setOnClickListener {
            musicViewModel.playPrev()
        }

        nextMusicButton.setOnLongClickListener {
            true
        }

        prevMusicButton.setOnLongClickListener {
            true
        }


        //for inserting music into the Database as a FAVORITE
        favoriteIcon.setOnClickListener {
            Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.music_added_to_favorites),
                    Toast.LENGTH_SHORT
            ).show()
            val image = musicViewModel.currentMedia().path.thumbnailToByteArray()
            if (image != null) {
                mediaViewModel.insertMedia(
                        MediaFileModel(
                                musicViewModel.currentMedia().id,
                                musicViewModel.currentMedia().title,
                                musicViewModel.currentMedia().path,
                                musicViewModel.currentMedia().duration,
                                byteArray = image,
                                artist = musicViewModel.currentMedia().artist
                        )
                )
            } else {
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.music)
                val byteArray = bitmap.toByteArray()
                mediaViewModel.insertMedia(
                        MediaFileModel(
                                musicViewModel.currentMedia().id,
                                musicViewModel.currentMedia().title,
                                musicViewModel.currentMedia().path,
                                musicViewModel.currentMedia().duration,
                                byteArray = byteArray,
                                artist = musicViewModel.currentMedia().artist
                        )
                )
            }
            heartIcon.visibility = View.VISIBLE
        }


        //for removing music from favorites list (Database)
        heartIcon.setOnClickListener {
            Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.music_removed_from_favorites),
                    Toast.LENGTH_SHORT
            ).show()
            mediaViewModel.deleteMedia(musicViewModel.currentMedia().id)
            heartIcon.visibility = View.GONE
        }
    }

}