package com.anahit.mediaplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint

private val MEDIA_PERMISSIONS_33_PLUS =
    arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO)
private val MEDIA_PERMISSIONS_LEGACY = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var isReady = false

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            isReady = true
            if (results.values.any { it }) {
                setContentView(R.layout.activity_main)
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { !isReady }

        if (hasMediaPermission()) {
            isReady = true
            setContentView(R.layout.activity_main)
        } else {
            requestPermissions.launch(mediaPermissions())
        }
    }

    private fun hasMediaPermission(): Boolean =
        mediaPermissions().any { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

    private fun mediaPermissions(): Array<String> =
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {
            MEDIA_PERMISSIONS_33_PLUS
        } else {
            MEDIA_PERMISSIONS_LEGACY
        }
}
