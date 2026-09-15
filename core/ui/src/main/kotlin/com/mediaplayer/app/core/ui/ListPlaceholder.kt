package com.mediaplayer.app.core.ui

import android.view.View
import android.widget.TextView

/** The empty-state view group (an icon + [text]) shown for [UiState.Empty] and [UiState.Error]. */
class ListPlaceholder(
    val container: View,
    val text: TextView,
)

/**
 * Shared visibility/text wiring for the common "loading list / content / empty-or-error
 * placeholder" screen shape used by both the music and video list screens.
 */
fun <T> UiState<T>.renderListVisibility(
    loadingIndicator: View,
    content: View,
    placeholder: ListPlaceholder,
    emptyMessageRes: Int,
    errorMessageRes: Int,
) {
    loadingIndicator.visibility = if (this is UiState.Loading) View.VISIBLE else View.GONE
    content.visibility = if (this is UiState.Success) View.VISIBLE else View.GONE
    placeholder.container.visibility = if (this is UiState.Empty || this is UiState.Error) View.VISIBLE else View.GONE

    when (this) {
        is UiState.Empty -> placeholder.text.text = placeholder.text.context.getString(emptyMessageRes)
        is UiState.Error -> placeholder.text.text = placeholder.text.context.getString(errorMessageRes)
        is UiState.Loading, is UiState.Success -> Unit
    }
}
