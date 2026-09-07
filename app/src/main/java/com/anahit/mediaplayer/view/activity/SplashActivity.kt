package com.anahit.mediaplayer.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.anahit.mediaplayer.R
import com.anahit.mediaplayer.view.fragment.MediaFragments
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // for changing status and navigation bar colors
        handleStatusAndNavigationBarColor()
        // checking corresponding permissions
        lifecycleScope.launch {
            handleCheckPermission()
        }
    }

    private suspend fun handleCheckPermission() {
        delay(1000)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // This case permission is ok
            goToNext()
        } else {
            // request corresponding permission
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun goToNext() {
        // This case permission is ok
        val mediaIntent = Intent(this, MediaActivity::class.java)
        mediaIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mediaIntent)
        finish()
    }


    private fun handleStatusAndNavigationBarColor() {
        window.statusBarColor = this.resources.getColor(R.color.transparent)
        window.navigationBarColor = this.resources.getColor(R.color.transparent)
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app.
                goToNext()
            } else {
                finish()
            }
        }
}