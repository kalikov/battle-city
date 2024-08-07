package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PointsTest {
    private lateinit var eventManager: EventManager
    private lateinit var pauseManager: PauseManager
    private lateinit var clock: TestClock

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        pauseManager = mock()
        clock = TestClock()
    }

    @Test
    fun `should destroy points after the specified duration`() {
        val points = createPoints(3)
        points.update()

        clock.tick(1)
        points.update()
        assertFalse(points.isDestroyed)

        clock.tick(1)
        points.update()
        assertFalse(points.isDestroyed)

        clock.tick(1)
        points.update()
        assertTrue(points.isDestroyed)
    }

    @Test
    fun `should respect pause in the points duration`() {
        eventManager = ConcurrentEventManager()
        val pauseListener = PauseListener(eventManager)
        pauseManager = pauseListener
        val points = createPoints(3)
        points.update()

        clock.tick(1)
        points.update()
        assertFalse(points.isDestroyed)

        pauseListener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        assertTrue(pauseManager.isPaused)

        clock.tick(100)
        points.update()
        assertFalse(points.isDestroyed)

        pauseListener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        assertFalse(pauseManager.isPaused)

        clock.tick(1)
        points.update()
        assertFalse(points.isDestroyed)

        clock.tick(1)
        points.update()
        assertTrue(points.isDestroyed)
    }

    @Test
    fun `should destroy points when not paused`() {
        val points = createPoints(1)
        points.update()

        clock.tick(1)
        points.update()
        assertTrue(points.isDestroyed)
    }

    @Test
    fun `should not destroy points when paused`() {
        eventManager = ConcurrentEventManager()
        pauseManager = PauseListener(eventManager)

        val points = createPoints(1)

        eventManager.fireEvent(Keyboard.KeyPressed(Keyboard.Key.START, 0))

        points.update()

        clock.tick(1)
        points.update()
        assertFalse(points.isDestroyed)
    }

    private fun createPoints(duration: Int): Points {
        return Points(eventManager, mock(), clock,  100, 0, 0, duration = duration)
    }
}