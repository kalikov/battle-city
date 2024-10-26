package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PauseListenerTest {
    private lateinit var eventManager: EventManager
    private lateinit var soundManager: SoundManager
    private lateinit var listener: PauseListener

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        soundManager = mock()

        listener = PauseListener(mockGame(eventManager = eventManager, soundManager = soundManager))
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(listener, setOf(Keyboard.KeyPressed::class))
    }

    @Test
    fun `should pause on start`() {
        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        assertTrue(listener.isPaused)
    }

    @Test
    fun `should resume on second start`() {
        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        assertFalse(listener.isPaused)
    }

    @Test
    fun `should handle sequence of events properly`() {
        assertFalse(listener.isPaused)
        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        assertTrue(listener.isPaused)
        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        assertFalse(listener.isPaused)
        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        assertTrue(listener.isPaused)
    }

    @Test
    fun `should fire events on pause state change`() {
        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        verify(eventManager).fireEvent(PauseManager.Start)

        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        verify(eventManager).fireEvent(PauseManager.End)
    }

    @Test
    fun `should play sound on pause start`() {
        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        verify(soundManager).play("pause")
    }

    @Test
    fun `should dispose`() {
        listener.dispose()
        verify(eventManager).removeSubscriber(listener, setOf(Keyboard.KeyPressed::class))
    }
}