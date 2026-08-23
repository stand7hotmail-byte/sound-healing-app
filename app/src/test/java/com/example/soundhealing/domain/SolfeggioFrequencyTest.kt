package com.example.soundhealing.domain

import org.junit.Assert.*
import org.junit.Test

class SolfeggioFrequencyTest {

    @Test
    fun `全ソルフェジオ周波数が定義されている`() {
        assertEquals(9, SolfeggioFrequency.ALL.size)
    }

    @Test
    fun `528Hzの説明が含まれている`() {
        val freq = SolfeggioFrequency.ALL.find { it.frequency == 528.0 }
        assertNotNull(freq)
        assertTrue(freq!!.description.isNotEmpty())
    }

    @Test
    fun `全周波数が正の値で合理範囲内`() {
        SolfeggioFrequency.ALL.forEach { freq ->
            assertTrue("${freq.name} の周波数が不正", freq.frequency > 0.0)
            assertTrue("${freq.name} の周波数が不正", freq.frequency <= 2000.0)
        }
    }

    @Test
    fun `全周波数に名前と絵文字がある`() {
        SolfeggioFrequency.ALL.forEach { freq ->
            assertTrue("name が空: ${freq.id}", freq.name.isNotEmpty())
            assertTrue("emoji が空: ${freq.id}", freq.emoji.isNotEmpty())
        }
    }
}
