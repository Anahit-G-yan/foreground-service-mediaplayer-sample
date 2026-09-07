package com.anahit.mediaplayer.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackSettingsRepositoryImplTest {
    private lateinit var tempFile: File
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: PlaybackSettingsRepositoryImpl

    @Before
    fun setUp() {
        tempFile = File.createTempFile("playback_settings_test", ".preferences_pb")
        dataStore =
            PreferenceDataStoreFactory.create(
                scope = CoroutineScope(UnconfinedTestDispatcher()),
                produceFile = { tempFile },
            )
        repository = PlaybackSettingsRepositoryImpl(dataStore)
    }

    @After
    fun tearDown() {
        tempFile.delete()
    }

    @Test
    fun `foreground playback defaults to false`() =
        runTest {
            repository.observeForegroundPlaybackEnabled().test {
                assertEquals(false, awaitItem())
            }
        }

    @Test
    fun `setForegroundPlaybackEnabled persists the value`() =
        runTest {
            repository.setForegroundPlaybackEnabled(true)

            repository.observeForegroundPlaybackEnabled().test {
                assertEquals(true, awaitItem())
            }
        }
}
