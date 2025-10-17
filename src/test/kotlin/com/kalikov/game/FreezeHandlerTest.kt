package com.kalikov.game

import com.kalikov.engine.EventManager
import com.kalikov.util.TestClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FreezeHandlerTest {
    private lateinit var eventManager: EventManager
    private lateinit var pauseManager: PauseManager
    private lateinit var clock: TestClock
    private lateinit var handler: FreezeHandler

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        pauseManager = mock()
        clock = TestClock()
        handler = FreezeHandler(eventManager, pauseManager, clock)
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(handler, arrayOf(PowerUpHandler.Freeze::class))
    }

    @Test
    fun `should activate on freeze`() {
        assertFalse(handler.isActive)
        handler.notify(PowerUpHandler.Freeze)
        assertTrue(handler.isActive)
    }

    @Test
    fun `should notify on unfreeze correctly`() {
        handler.notify(PowerUpHandler.Freeze)

        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)
        assertTrue(handler.isActive)

        clock.tick(3000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)
        assertTrue(handler.isActive)

        clock.tick(6000)
        handler.update()
        verify(eventManager).fireEvent(FreezeHandler.Unfreeze)
        assertFalse(handler.isActive)

        reset(eventManager)

        clock.tick(6000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)

        clock.tick(15000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)

        clock.tick(15000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)

        handler.notify(PowerUpHandler.Freeze)

        clock.tick(3000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)

        clock.tick(3000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)

        clock.tick(3000)
        handler.update()
        verify(eventManager).fireEvent(FreezeHandler.Unfreeze)
    }

    @Test
    fun `should not unfreeze when paused`() {
        whenever(pauseManager.isPaused).thenReturn(true)

        handler.notify(PowerUpHandler.Freeze)
        assertTrue(handler.isActive)

        clock.tick(3000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)

        clock.tick(15000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)

        clock.tick(15000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)
    }

    @Test
    fun `should handle pause end correctly`() {
        handler.notify(PowerUpHandler.Freeze)

        clock.tick(3000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)

        whenever(pauseManager.isPaused).thenReturn(true)
        handler.update()
        assertTrue(handler.isActive)

        clock.tick(15000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)

        clock.tick(15000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)

        whenever(pauseManager.isPaused).thenReturn(false)
        handler.update()
        assertTrue(handler.isActive)

        clock.tick(3000)
        handler.update()
        verify(eventManager, never()).fireEvent(FreezeHandler.Unfreeze)

        clock.tick(3000)
        handler.update()
        verify(eventManager).fireEvent(FreezeHandler.Unfreeze)
        assertFalse(handler.isActive)
    }
}