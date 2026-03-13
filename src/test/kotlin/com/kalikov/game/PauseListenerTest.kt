package com.kalikov.game

import com.kalikov.engine.EventManager
import com.kalikov.engine.Keyboard
import com.kalikov.engine.SoundManager
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
        soundManager = mockSoundManager()

        listener = PauseListener(mockGame(eventManager = eventManager, soundManager = soundManager))
    }

    @Test
    fun `should subscribe`() {
        listener.activate()
        verify(eventManager).addSubscriber(listener, arrayOf(Keyboard.KeyPressed::class))
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
        assertFalse(listener.isPaused)

        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        assertTrue(listener.isPaused)

        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        assertFalse(listener.isPaused)
    }

    @Test
    fun `should play sound on pause start`() {
        listener.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))
        verify(soundManager.pause).play()
    }

    @Test
    fun `should unsubscribe`() {
        listener.deactivate()
        verify(eventManager).removeSubscriber(listener, arrayOf(Keyboard.KeyPressed::class))
    }
}