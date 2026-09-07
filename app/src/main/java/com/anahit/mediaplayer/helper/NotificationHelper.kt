package com.anahit.mediaplayer.helper

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anahit.mediaplayer.R
import com.anahit.mediaplayer.constant.Constant
import com.anahit.mediaplayer.model.MediaFileModel
import com.anahit.mediaplayer.service.MusicService
import com.anahit.mediaplayer.view.activity.MediaActivity


/**
 *  // TODO: Need to add stop  button on notification view
 */
object NotificationHelper {
    private lateinit var modelMedia: MediaFileModel


    fun startForeground(service: Service, mMediaFileModel: MediaFileModel, pauseIcon: Int = R.drawable.pause_button) {
        val mediaSession = MediaSessionCompat(service, "mediaSession")
        createNotificationChannel(service)
        modelMedia = mMediaFileModel

        val notificationIntent = Intent(service, MediaActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(service, 0, notificationIntent, 0)


        val stopIntent = Intent(service, MusicService::class.java)
        stopIntent.action = Constant.MUSIC_SERVICE_STOP

        val pauseIntent = Intent(service, MusicService::class.java)
        pauseIntent.action = Constant.MUSIC_SERVICE_PAUSE

        val pause = PendingIntent.getService(service, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val stop = PendingIntent.getService(service, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)


        val builder = NotificationCompat.Builder(service, "CHANNEL_ID")
            .addAction(pauseIcon, "Pause", pause)
            .addAction(R.drawable.close, "Stop", stop)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1 /* #0: pause button \*/  /* #1: close button */)
                    .setMediaSession(mediaSession.getSessionToken())
            )
            .setSmallIcon(R.drawable.music)
            .setContentTitle(modelMedia.title)
            .setContentText(modelMedia.artist)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)


        val notification = builder.build()
        with(NotificationManagerCompat.from(service)) {
            // notificationId is a unique int for each notification that you must define
            notify(4, notification)
        }
        service.startForeground(4, notification)
    }


    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}