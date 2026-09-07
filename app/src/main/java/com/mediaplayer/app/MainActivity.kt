package com.mediaplayer.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
                showPermissionRationale()
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

    /**
     * Explains why the permission is needed instead of silently closing: the user may have
     * dismissed the system prompt without understanding what it was for, or denied it
     * permanently, in which case the only way back in is the app's system settings screen.
     */
    private fun showPermissionRationale() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_rationale_title)
            .setMessage(R.string.permission_rationale_message)
            .setCancelable(false)
            .setPositiveButton(R.string.action_open_settings) { _, _ -> openAppSettings() }
            .setNegativeButton(R.string.action_exit) { _, _ -> finish() }
            .show()
    }

    private fun openAppSettings() {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        startActivity(intent)
        finish()
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
