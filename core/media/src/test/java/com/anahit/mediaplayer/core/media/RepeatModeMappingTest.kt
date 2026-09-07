package com.anahit.mediaplayer.core.media

import androidx.media3.common.Player
import org.junit.Assert.assertEquals
import org.junit.Test

class RepeatModeMappingTest {
    @Test
    fun `domain repeat mode maps to the matching player constant`() {
        assertEquals(Player.REPEAT_MODE_OFF, RepeatMode.OFF.toPlayerRepeatMode())
        assertEquals(Player.REPEAT_MODE_ONE, RepeatMode.ONE.toPlayerRepeatMode())
        assertEquals(Player.REPEAT_MODE_ALL, RepeatMode.ALL.toPlayerRepeatMode())
    }

    @Test
    fun `player repeat constant maps back to the matching domain mode`() {
        assertEquals(RepeatMode.OFF, Player.REPEAT_MODE_OFF.toDomainRepeatMode())
        assertEquals(RepeatMode.ONE, Player.REPEAT_MODE_ONE.toDomainRepeatMode())
        assertEquals(RepeatMode.ALL, Player.REPEAT_MODE_ALL.toDomainRepeatMode())
    }
}
