package com.anahit.mediaplayer.data.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaStoreRepositoryImplTest {
    @Test
    fun `mp3 longer than the voice-memo threshold is eligible`() {
        assertTrue(isEligibleTrack("/music/song.mp3", durationMillis = 120_000))
    }

    @Test
    fun `mp3 shorter than the voice-memo threshold is filtered out`() {
        assertFalse(isEligibleTrack("/music/notification.mp3", durationMillis = 2_000))
    }

    @Test
    fun `non-mp3 file is filtered out regardless of duration`() {
        assertFalse(isEligibleTrack("/music/song.wav", durationMillis = 120_000))
    }

    @Test
    fun `mp3 extension is matched case-insensitively`() {
        assertTrue(isEligibleTrack("/music/SONG.MP3", durationMillis = 120_000))
    }
}
