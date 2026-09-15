package com.mediaplayer.app.core.ui

import android.net.Uri

/**
 * Cross-module navigation targets. The nav graph lives in :app, so Safe Args' generated
 * Directions/Args classes - and even plain `R.id` action/destination constants - aren't reachable
 * from feature modules (non-transitive R classes scope every resource, nav graph IDs included, to
 * the module that declares them). Deep links sidestep that entirely: they're runtime strings, not
 * compile-time resource references, which is why Android's own modularization guide recommends
 * them for exactly this situation.
 */
object NavDestinations {
    private const val SCHEME = "mediaplayer"

    /** Bundle/argument key the video player destination's path arg is read back under. */
    const val ARG_VIDEO_PATH = "videoPath"

    val player: Uri =
        Uri
            .Builder()
            .scheme(SCHEME)
            .authority("player")
            .build()

    fun videoPlayer(path: String): Uri =
        Uri
            .Builder()
            .scheme(SCHEME)
            .authority("video")
            .appendQueryParameter("path", path)
            .build()
}
