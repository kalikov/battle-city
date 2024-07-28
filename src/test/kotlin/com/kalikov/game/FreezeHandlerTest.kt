package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FreezeHandlerTest {
    private lateinit var eventManager: EventManager
    private lateinit var pauseListener: PauseListener
    private lateinit var clock: TestClock
    private lateinit var timer: FreezeHandler

    private lateinit var unfreezeListener: EventSubscriber

    @BeforeEach
    fun beforeEach() {
        eventManager = ConcurrentEventManager()
        pauseListener = PauseListener(eventManager)
        clock = TestClock()
        timer = FreezeHandler(eventManager, clock)

        unfreezeListener = mock()
        eventManager.addSubscriber(unfreezeListener, setOf(FreezeHandler.Unfreeze::class))
    }

    @Test
    fun `should subscribe`() {
        eventManager = mock()
        timer = FreezeHandler(eventManager, mock())
        verify(eventManager).addSubscriber(timer, setOf(PowerUpHandler.Freeze::class))
    }

    @Test
    fun `should activate on freeze`() {
        assertFalse(timer.isActive)
        timer.notify(PowerUpHandler.Freeze)
        assertTrue(timer.isActive)
    }

    @Test
    fun `should notify on unfreeze correctly`() {
        timer.notify(PowerUpHandler.Freeze)

        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)
        assertTrue(timer.isActive)

        clock.tick(3000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)
        assertTrue(timer.isActive)

        clock.tick(6000)
        timer.update()
        verify(unfreezeListener).notify(FreezeHandler.Unfreeze)
        assertFalse(timer.isActive)

        reset(unfreezeListener)

        clock.tick(6000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)

        clock.tick(15000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)

        clock.tick(15000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)

        timer.notify(PowerUpHandler.Freeze)

        clock.tick(3000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)

        clock.tick(3000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)

        clock.tick(3000)
        timer.update()
        verify(unfreezeListener).notify(FreezeHandler.Unfreeze)
    }

    @Test
    fun `should not unfreeze when paused`() {
        pauseListener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))

        timer.notify(PowerUpHandler.Freeze)
        assertTrue(timer.isActive)

        clock.tick(3000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)

        clock.tick(15000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)

        clock.tick(15000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)
    }

    @Test
    fun `should handle pause end correctly`() {
        timer.notify(PowerUpHandler.Freeze)

        clock.tick(3000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)

        pauseListener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        assertTrue(timer.isActive)

        clock.tick(15000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)

        clock.tick(15000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)

        pauseListener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        assertTrue(timer.isActive)

        clock.tick(3000)
        timer.update()
        verify(unfreezeListener, never()).notify(FreezeHandler.Unfreeze)

        clock.tick(3000)
        timer.update()
        verify(unfreezeListener).notify(FreezeHandler.Unfreeze)
        assertFalse(timer.isActive)
    }
}