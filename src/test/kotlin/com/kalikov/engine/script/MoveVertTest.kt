package com.kalikov.engine.script

import com.kalikov.engine.px
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class MoveVertTest {
    private lateinit var moveable: Moveable
    private lateinit var move: MoveVert

    @BeforeEach
    fun beforeEach() {
        moveable = mock()
        move = MoveVert(moveable)
    }

    @Test
    fun `should get moveable y property`() {
        whenever(moveable.y).thenReturn(10.px)
        assertEquals(10, move.value)
    }

    @Test
    fun `should set moveable y property`() {
        move.value = 10
        verify(moveable).y = 10.px
    }
}