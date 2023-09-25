package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnimationTest {
    private lateinit var clock: TestClock

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
    }

    @Test
    fun `should complete animation without loop`() {
        val animation = Animation.basic(frameSequenceOf(1, 2), clock, 1)
        animation.restart()
        assertEquals(1, animation.frame)
        assertFalse(animation.isCompleted)

        clock.tick(1)
        animation.update()
        assertEquals(2, animation.frame)
        assertFalse(animation.isCompleted)

        clock.tick(1)
        animation.update()
        assertEquals(2, animation.frame)
        assertTrue(animation.isCompleted)
    }

    @Test
    fun `should loop animation`() {
        val animation = Animation.basic(frameLoopOf(1, 2), clock, 1)
        animation.restart()
        assertEquals(1, animation.frame)
        assertFalse(animation.isCompleted)

        clock.tick(1)
        animation.update()
        assertEquals(2, animation.frame)
        assertFalse(animation.isCompleted)

        clock.tick(1)
        animation.update()
        assertEquals(1, animation.frame)
        assertFalse(animation.isCompleted)
    }

    @Test
    fun `should respect frame duration`() {
        val animation = Animation.basic(frameSequenceOf(1, 2), clock, 2)
        animation.restart()
        assertEquals(1, animation.frame)

        clock.tick(1)
        animation.update()
        assertEquals(1, animation.frame)

        clock.tick(1)
        animation.update()
        assertEquals(2, animation.frame)

        clock.tick(1)
        animation.update()
        assertEquals(2, animation.frame)
        assertFalse(animation.isCompleted)

        clock.tick(1)
        animation.update()
        assertEquals(2, animation.frame)
        assertTrue(animation.isCompleted)
    }
}