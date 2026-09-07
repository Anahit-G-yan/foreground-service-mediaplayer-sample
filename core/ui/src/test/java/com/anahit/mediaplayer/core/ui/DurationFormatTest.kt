package com.anahit.mediaplayer.core.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class DurationFormatTest {
    @Test
    fun `zero millis formats as 0 colon 00`() {
        assertEquals("0:00", 0L.formatAsMinutesAndSeconds())
    }

    @Test
    fun `seconds under a minute pad to two digits`() {
        assertEquals("0:05", 5_000L.formatAsMinutesAndSeconds())
    }

    @Test
    fun `minutes and seconds format together`() {
        assertEquals("2:03", 123_000L.formatAsMinutesAndSeconds())
    }

    @Test
    fun `partial second is truncated, not rounded`() {
        assertEquals("0:59", 59_999L.formatAsMinutesAndSeconds())
    }

    @Test
    fun `minutes past nine are not zero padded`() {
        assertEquals("10:00", 600_000L.formatAsMinutesAndSeconds())
    }
}
