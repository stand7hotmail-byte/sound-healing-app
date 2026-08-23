package com.example.soundhealing.domain

import org.junit.Assert.*
import org.junit.Test

class NatureSoundTest {

    @Test
    fun `全自然音が定義されている`() {
        assertEquals(5, NatureSound.ALL.size)
    }

    @Test
    fun `日本語名がすべて空ではない`() {
        NatureSound.ALL.forEach { sound ->
            assertTrue("名前が空: ${sound.id}", sound.name.isNotEmpty())
        }
    }

    @Test
    fun `全自然音に説明と絵文字がある`() {
        NatureSound.ALL.forEach { sound ->
            assertTrue("説明が空: ${sound.name}", sound.description.isNotEmpty())
            assertTrue("絵文字が空: ${sound.name}", sound.emoji.isNotEmpty())
        }
    }
}
