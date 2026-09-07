package com.anahit.mediaplayer.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val PLAYBACK_PREFERENCES_NAME = "playback_preferences"

val Context.playbackDataStore: DataStore<Preferences> by preferencesDataStore(name = PLAYBACK_PREFERENCES_NAME)
