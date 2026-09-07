package com.anahit.mediaplayer.core.ui

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Pads this view's top/bottom by the status/navigation bar insets, added on top of whatever
 * padding it already has in XML. Needed because targetSdk 35+ draws content edge-to-edge by
 * default, so system bars would otherwise overlap top/bottom UI.
 */
fun View.applySystemBarsPadding(
    top: Boolean = false,
    bottom: Boolean = false,
) {
    val initialPaddingTop = paddingTop
    val initialPaddingBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(
            view.paddingLeft,
            if (top) initialPaddingTop + bars.top else initialPaddingTop,
            view.paddingRight,
            if (bottom) initialPaddingBottom + bars.bottom else initialPaddingBottom,
        )
        insets
    }
}
