package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class MoveHorzTest {
    private lateinit var moveable: Moveable
    private lateinit var move: MoveHorz

    @BeforeEach
    fun beforeEach() {
        moveable = mock()
        move = MoveHorz(moveable)
    }

    @Test
    fun `should get moveable x property`() {
        whenever(moveable.x).thenReturn(10)
        assertEquals(10, move.value)
    }

    @Test
    fun `should set moveable x property`() {
        move.value = 10
        verify(moveable).x = 10
    }
}