package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PointsTest {
    private lateinit var game: Game
    private lateinit var pauseManager: PauseManager
    private lateinit var clock: TestClock

    @BeforeEach
    fun beforeEach() {
        pauseManager = mock()
        clock = TestClock()
        game = mockGame(clock = clock)
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
        whenever(game.eventManager).thenReturn(ConcurrentEventManager())
        val pauseListener = PauseListener(game)
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
        whenever(game.eventManager).thenReturn(ConcurrentEventManager())
        pauseManager = PauseListener(game)

        val points = createPoints(1)

        game.eventManager.fireEvent(Keyboard.KeyPressed(Keyboard.Key.START, 0))

        points.update()

        clock.tick(1)
        points.update()
        assertFalse(points.isDestroyed)
    }

    private fun createPoints(duration: Int): Points {
        return Points(game, 100, px(0), px(0), duration = duration)
    }
}