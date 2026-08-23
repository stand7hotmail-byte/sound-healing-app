package com.example.soundhealing.domain

import org.junit.Assert.*
import org.junit.Test

class BrainwaveTypeTest {

    @Test
    fun `デルタ波の範囲が正しい`() {
        val delta = BrainwaveType.DELTA
        assertEquals(0.5, delta.frequencyRangeHz.first, 0.01)
        assertEquals(4.0, delta.frequencyRangeHz.second, 0.01)
    }

    @Test
    fun `アルファ波の範囲が正しい`() {
        val alpha = BrainwaveType.ALPHA
        assertEquals(8.0, alpha.frequencyRangeHz.first, 0.01)
        assertEquals(13.0, alpha.frequencyRangeHz.second, 0.01)
    }

    @Test
    fun `全タイプに説明がある`() {
        BrainwaveType.values().forEach { type ->
            assertTrue("説明が空: ${type.name}", type.description.isNotEmpty())
        }
    }

    @Test
    fun `4種類の脳波が定義されている`() {
        assertEquals(4, BrainwaveType.values().size)
    }
}
