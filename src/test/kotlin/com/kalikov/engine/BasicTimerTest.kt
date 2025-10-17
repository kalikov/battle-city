package com.kalikov.engine

import com.kalikov.util.TestClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BasicTimerTest {
    private lateinit var clock: TestClock
    private lateinit var callback: TestCallback
    private lateinit var timer: BasicTimer

    @BeforeEach
    fun `before method`() {
        clock = TestClock()
        callback = mock()
        timer = BasicTimer(clock, 1, callback::invoke)
    }

    @Test
    fun `update should do nothing when timer is stopped`() {
        clock.tick(100)
        timer.update()

        verifyNoInteractions(callback)
    }

    @Test
    fun `update should invoke callback`() {
        timer.restart()
        clock.tick(100)
        timer.update()

        verify(callback).invoke(100)
    }

    @Test
    fun `update should do nothing when timer is paused after update`() {
        timer.restart()
        clock.tick(100)
        timer.update()

        reset(callback)

        timer.pause()
        clock.tick(100)
        timer.update()

        verifyNoInteractions(callback)
    }

    @Test
    fun `update should do nothing when timer is paused before update`() {
        timer.restart()
        clock.tick(100)
        timer.pause()

        clock.tick(100)
        timer.update()

        verifyNoInteractions(callback)
    }

    @Test
    fun `resume should respect ticked time before update`() {
        timer.restart()
        clock.tick(10)
        timer.pause()

        clock.tick(20)

        timer.resume()
        verifyNoInteractions(callback)

        clock.tick(30)
        timer.update()

        verify(callback).invoke(40)
    }

    @Test
    fun `should have stopped default state`() {
        assertTrue(timer.isStopped)
        assertFalse(timer.isPaused)
    }

    @Test
    fun `should have running state after restart`() {
        timer.restart()
        assertFalse(timer.isStopped)
        assertFalse(timer.isPaused)
    }

    @Test
    fun `pause and resume should not change stopped state`() {
        timer.pause()
        assertTrue(timer.isStopped)
        assertFalse(timer.isPaused)
        timer.resume()
        assertTrue(timer.isStopped)
        assertFalse(timer.isPaused)
    }

    @Test
    fun `pause should change running state`() {
        timer.restart()
        timer.pause()
        assertFalse(timer.isStopped)
        assertTrue(timer.isPaused)
    }

    @Test
    fun `resume should change paused state`() {
        timer.restart()
        timer.pause()
        timer.resume()
        assertFalse(timer.isStopped)
        assertFalse(timer.isPaused)
    }

    @Test
    fun `resume should not change running state`() {
        timer.restart()
        timer.resume()
        assertFalse(timer.isStopped)
        assertFalse(timer.isPaused)
    }

    @Test
    fun `stop should change running state`() {
        timer.restart()
        timer.stop()
        assertTrue(timer.isStopped)
        assertFalse(timer.isPaused)
    }

    @Test
    fun `stop should change paused state`() {
        timer.restart()
        timer.pause()
        timer.stop()
        assertTrue(timer.isStopped)
        assertFalse(timer.isPaused)
    }

    private interface TestCallback {
        fun invoke(count: Int)
    }
}