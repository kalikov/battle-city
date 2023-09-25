package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.test.assertEquals

class MoveFnTest {
    private class TestProperty(override var value: Int = 0) : MoveProperty

    private lateinit var clock: TestClock

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
    }

    @Test
    fun `should move using positive increment step - 1`() {
        val obj = TestProperty()
        val listener: ScriptCallback = mock()
        val moveFn = MoveFn(obj, 3, 320, listener, clock)
        moveFn.update()

        assertEquals(0, obj.value)

        clock.tick(110)
        moveFn.update()
        assertEquals(1, obj.value)
        verify(listener, never()).actionCompleted()

        clock.tick(110)
        moveFn.update()
        assertEquals(2, obj.value)
        verify(listener, never()).actionCompleted()

        clock.tick(110)
        moveFn.update()
        assertEquals(3, obj.value)
        verify(listener).actionCompleted()

        clock.tick(110)
        moveFn.update()
        assertEquals(3, obj.value)
        verifyNoMoreInteractions(listener)
    }

    @Test
    fun `should move using positive increment step - 2`() {
        val obj = TestProperty()
        val listener: ScriptCallback = mock()
        val moveFn = MoveFn(obj, 3, 2, listener, clock)
        moveFn.update()

        assertEquals(0, obj.value)

        clock.tick(1)
        moveFn.update()
        assertEquals(1, obj.value)
        verify(listener, never()).actionCompleted()

        clock.tick(1)
        moveFn.update()
        assertEquals(3, obj.value)
        verify(listener).actionCompleted()

        clock.tick(1)
        moveFn.update()
        assertEquals(3, obj.value)
        verifyNoMoreInteractions(listener)
    }

    @Test
    fun `should move using positive increment at once`() {
        val obj = TestProperty()
        val listener: ScriptCallback = mock()
        val moveFn = MoveFn(obj, 3, 1, listener, clock)
        moveFn.update()

        assertEquals(0, obj.value)

        clock.tick(1)
        moveFn.update()
        assertEquals(3, obj.value)
        verify(listener).actionCompleted()
    }

    @Test
    fun `should move using positive positive increment`() {
        val obj = TestProperty()
        val listener: ScriptCallback = mock()
        val moveFn = MoveFn(obj, 40, 6, listener, clock)
        moveFn.update()

        assertEquals(0, obj.value)

        clock.tick(1)
        moveFn.update()
        assertEquals(6, obj.value)
        verify(listener, never()).actionCompleted()

        clock.tick(1)
        moveFn.update()
        assertEquals(13, obj.value)
        verify(listener, never()).actionCompleted()

        clock.tick(1)
        moveFn.update()
        assertEquals(20, obj.value)
        verify(listener, never()).actionCompleted()

        clock.tick(1)
        moveFn.update()
        assertEquals(26, obj.value)
        verify(listener, never()).actionCompleted()

        clock.tick(1)
        moveFn.update()
        assertEquals(33, obj.value)
        verify(listener, never()).actionCompleted()

        clock.tick(1)
        moveFn.update()
        assertEquals(40, obj.value)
        verify(listener).actionCompleted()
    }

    @Test
    fun `should move using negative increment step - 1`() {
        val obj = TestProperty(3)
        val listener: ScriptCallback = mock()
        val moveFn = MoveFn(obj, 0, 90, listener, clock)
        moveFn.update()

        assertEquals(3, obj.value)

        clock.tick(30)
        moveFn.update()
        assertEquals(2, obj.value)
        verify(listener, never()).actionCompleted()

        clock.tick(30)
        moveFn.update()
        assertEquals(1, obj.value)
        verify(listener, never()).actionCompleted()

        clock.tick(30)
        moveFn.update()
        assertEquals(0, obj.value)
        verify(listener).actionCompleted()

        clock.tick(100)
        moveFn.update()
        assertEquals(0, obj.value)
        verifyNoMoreInteractions(listener)
    }

    @Test
    fun `should move using negative increment at once`() {
        val obj = TestProperty(3)
        val listener: ScriptCallback = mock()
        val moveFn = MoveFn(obj, 0, 1, listener, clock)
        moveFn.update()

        assertEquals(3, obj.value)

        clock.tick(1)
        moveFn.update()
        assertEquals(0, obj.value)
        verify(listener).actionCompleted()
    }
}