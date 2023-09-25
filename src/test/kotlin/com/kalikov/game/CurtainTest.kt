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
        curtain.height = 3
        assertEquals(0, curtain.position)
        curtain.fall()
        assertEquals(1, curtain.position)
        curtain.fall()
        assertEquals(2, curtain.position)
        curtain.fall()
        assertEquals(3, curtain.position)
        curtain.fall()
        assertEquals(3, curtain.position)
    }

    @Test
    fun `should rise`() {
        curtain.height = 3
        curtain.position = 3
        assertEquals(3, curtain.position)
        curtain.rise()
        assertEquals(2, curtain.position)
        curtain.rise()
        assertEquals(1, curtain.position)
        curtain.rise()
        assertEquals(0, curtain.position)
        curtain.rise()
        assertEquals(0, curtain.position)
    }
}