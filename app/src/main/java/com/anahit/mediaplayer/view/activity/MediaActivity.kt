package com.anahit.mediaplayer.view.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.anahit.mediaplayer.R
import com.anahit.mediaplayer.handler.MusicTimerHandler
import com.anahit.mediaplayer.service.MusicService
import com.anahit.mediaplayer.view.fragment.MediaFragments
import com.anahit.mediaplayer.viewmodel.music.MusicViewModel

class MediaActivity : BaseActivity() {

    lateinit var mMusicService: MusicService
    private var mBound: Boolean = false

    private val musicViewModel: MusicViewModel by viewModels()


    lateinit var musicTimerHandler: MusicTimerHandler


    ////    /** Defines callbacks for service binding, passed to bindService()  */
    val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to MusicService, cast the IBinder and get MusicService instance
            val binder = service as MusicService.LocalBinder
            mMusicService = binder.getService()
            musicTimerHandler = mMusicService.musicTimerHandler
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runService()
        //TODO: need to create base Activity and move openFragment function into there
        openFragment(R.id.fragmentContainer, MediaFragments())
    }


    private fun runService() {
        val serviceIntent = Intent(this, MusicService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent)
            startService(serviceIntent)
        } else {
            // Bind to LocalService
            serviceIntent.also { intent ->
                startService(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Bind to MusicService
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!musicViewModel.checkService()) {
            mMusicService.stopSelf()
        }
    }

}