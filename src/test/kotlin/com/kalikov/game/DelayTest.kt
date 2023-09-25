package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class DelayTest {
    @Test
    fun `should complete delay`() {
        val script: ScriptCallback = mock()
        val clock = TestClock()
        val delay = Delay(script, 3, clock)
        delay.update()
        verify(script, never()).actionCompleted()

        clock.tick(1)
        delay.update()
        verify(script, never()).actionCompleted()

        clock.tick(1)
        delay.update()
        verify(script, never()).actionCompleted()

        clock.tick(1)
        delay.update()
        verify(script).actionCompleted()
    }
}