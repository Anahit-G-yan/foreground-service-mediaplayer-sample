package com.anahit.mediaplayer.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.anahit.mediaplayer.domain.repository.PlaybackSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaybackSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : PlaybackSettingsRepository {
    private object Keys {
        val FOREGROUND_PLAYBACK_ENABLED = booleanPreferencesKey("foreground_playback_enabled")
    }

    override fun observeForegroundPlaybackEnabled(): Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[Keys.FOREGROUND_PLAYBACK_ENABLED] ?: false }

    override suspend fun setForegroundPlaybackEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[Keys.FOREGROUND_PLAYBACK_ENABLED] = enabled }
    }
}
