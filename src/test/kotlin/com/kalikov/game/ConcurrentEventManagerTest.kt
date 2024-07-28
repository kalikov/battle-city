package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class ConcurrentEventManagerTest {
    @Test
    fun `should notify subscribers about events`() {
        val event1 = Keyboard.KeyPressed(Keyboard.Key.START, 0)
        val event2 = Keyboard.KeyReleased(Keyboard.Key.START, 0)

        val eventManager = ConcurrentEventManager()

        val subscriber1: EventSubscriber = mock()
        val subscriber2: EventSubscriber = mock()

        eventManager.addSubscriber(subscriber1, setOf(Keyboard.KeyPressed::class))
        eventManager.addSubscriber(subscriber2, setOf(Keyboard.KeyPressed::class, Keyboard.KeyReleased::class))

        eventManager.fireEvent(event1)
        eventManager.fireEvent(event2)

        verify(subscriber1).notify(event1)
        verify(subscriber2).notify(event1)
        verify(subscriber1, never()).notify(event2)
        verify(subscriber2).notify(event2)
    }

    @Test
    fun `should remove subscriber`() {
        val event1 = Keyboard.KeyPressed(Keyboard.Key.START, 0)
        val event2 = Keyboard.KeyReleased(Keyboard.Key.START, 0)

        val eventManager = ConcurrentEventManager()

        val subscriber1: EventSubscriber = mock()
        val subscriber2: EventSubscriber = mock()

        eventManager.addSubscriber(subscriber1, setOf(Keyboard.KeyPressed::class))
        eventManager.addSubscriber(subscriber2, setOf(Keyboard.KeyPressed::class, Keyboard.KeyReleased::class))

        eventManager.removeSubscriber(subscriber1, setOf(Keyboard.KeyPressed::class))

        eventManager.fireEvent(event1)
        eventManager.fireEvent(event2)

        verify(subscriber1, never()).notify(any())
        verify(subscriber2).notify(event1)
        verify(subscriber2).notify(event2)
    }
}