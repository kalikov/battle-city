package com.kalikov.game

import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CurtainTest {
    private lateinit var curtain: Curtain

    @BeforeEach
    fun beforeEach() {
        curtain = Curtain()
    }

    @Test
    fun `should fall`() {
        curtain.height = px(3)
        assertEquals(px(0), curtain.position)
        curtain.fall()
        assertEquals(px(1), curtain.position)
        curtain.fall()
        assertEquals(px(2), curtain.position)
        curtain.fall()
        assertEquals(px(3), curtain.position)
        curtain.fall()
        assertEquals(px(3), curtain.position)
    }

    @Test
    fun `should rise`() {
        curtain.height = px(3)
        curtain.position = px(3)
        assertEquals(px(3), curtain.position)
        curtain.rise()
        assertEquals(px(2), curtain.position)
        curtain.rise()
        assertEquals(px(1), curtain.position)
        curtain.rise()
        assertEquals(px(0), curtain.position)
        curtain.rise()
        assertEquals(px(0), curtain.position)
    }
}