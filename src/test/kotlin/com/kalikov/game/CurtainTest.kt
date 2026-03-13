package com.kalikov.game

import com.kalikov.engine.px
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
        curtain.height = 3.px
        assertEquals(0.px, curtain.position)
        curtain.fall()
        assertEquals(1.px, curtain.position)
        curtain.fall()
        assertEquals(2.px, curtain.position)
        curtain.fall()
        assertEquals(3.px, curtain.position)
        curtain.fall()
        assertEquals(3.px, curtain.position)
    }

    @Test
    fun `should rise`() {
        curtain.height = 3.px
        curtain.position = 3.px
        assertEquals(3.px, curtain.position)
        curtain.rise()
        assertEquals(2.px, curtain.position)
        curtain.rise()
        assertEquals(1.px, curtain.position)
        curtain.rise()
        assertEquals(0.px, curtain.position)
        curtain.rise()
        assertEquals(0.px, curtain.position)
    }
}