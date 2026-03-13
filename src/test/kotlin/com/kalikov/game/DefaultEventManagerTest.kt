package com.kalikov.game

import com.kalikov.engine.DefaultEventManager
import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.Keyboard
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.io.PrintStream

class DefaultEventManagerTest {
    data object EventA : Event()
    data object EventB : Event()

    @Test
    fun `should notify subscribers about events`() {
        val event1 = Keyboard.KeyPressed(Keyboard.Key.START, 0)
        val event2 = Keyboard.KeyReleased(Keyboard.Key.START, 0)

        val eventManager = DefaultEventManager()

        val subscriber1: EventSubscriber = mock {
            on { identity } doReturn 1
        }
        val subscriber2: EventSubscriber = mock {
            on { identity } doReturn 2
        }

        eventManager.addSubscriber(subscriber1, arrayOf(Keyboard.KeyPressed::class))
        eventManager.addSubscriber(subscriber2, arrayOf(Keyboard.KeyPressed::class, Keyboard.KeyReleased::class))

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

        val eventManager = DefaultEventManager()

        val subscriber1: EventSubscriber = mock {
            on { identity } doReturn 1
        }
        val subscriber2: EventSubscriber = mock {
            on { identity } doReturn 2
        }

        eventManager.addSubscriber(subscriber1, arrayOf(Keyboard.KeyPressed::class))
        eventManager.addSubscriber(subscriber2, arrayOf(Keyboard.KeyPressed::class, Keyboard.KeyReleased::class))

        eventManager.removeSubscriber(subscriber1, arrayOf(Keyboard.KeyPressed::class))

        eventManager.fireEvent(event1)
        eventManager.fireEvent(event2)

        verify(subscriber1, never()).notify(any())
        verify(subscriber2).notify(event1)
        verify(subscriber2).notify(event2)
    }

    @Test
    fun `should register subscriber for specific events`() {
        val manager = DefaultEventManager()
        val subscriber: EventSubscriber = mock()

        manager.addSubscriber(subscriber, arrayOf(EventA::class, EventB::class))

        manager.fireEvent(EventA)
        manager.fireEvent(EventB)

        verify(subscriber).notify(EventA)
        verify(subscriber).notify(EventB)
        verifyNoMoreInteractions(subscriber)
    }

    @Test
    fun `should unregister subscriber for specific events`() {
        val manager = DefaultEventManager()
        val subscriber: EventSubscriber = mock()

        manager.addSubscriber(subscriber, arrayOf(EventA::class, EventB::class))
        manager.removeSubscriber(subscriber, arrayOf(EventA::class))

        manager.fireEvent(EventA)
        manager.fireEvent(EventB)

        verify(subscriber, times(2)).identity
        verify(subscriber).notify(EventB)
        verifyNoMoreInteractions(subscriber)
    }

    @Test
    fun `should do nothing if no subscribers are registered`() {
        val manager = DefaultEventManager()

        manager.fireEvent(EventA)
    }

    @Test
    fun `should allow multiple subscribers for the same event`() {
        val manager = DefaultEventManager()
        val subscriber1: EventSubscriber = mock {
            on { identity } doReturn 1
        }
        val subscriber2: EventSubscriber = mock {
            on { identity } doReturn 2
        }

        manager.addSubscriber(subscriber1, arrayOf(EventA::class))
        manager.addSubscriber(subscriber2, arrayOf(EventA::class))

        manager.fireEvent(EventA)

        verify(subscriber1).notify(EventA)
        verify(subscriber1, atLeastOnce()).identity
        verify(subscriber2).notify(EventA)
        verify(subscriber2, atLeastOnce()).identity
        verifyNoMoreInteractions(subscriber1, subscriber2)
    }

    @Test
    fun `should clear dangling subscriptions on destroy`() {
        val out: PrintStream = mock()
        val manager = DefaultEventManager(out)
        val subscriber1: EventSubscriber = mock {
            on { identity } doReturn 1
        }
        val subscriber2: EventSubscriber = mock {
            on { identity } doReturn 2
        }

        manager.addSubscriber(subscriber1, arrayOf(EventA::class))
        manager.addSubscriber(subscriber2, arrayOf(EventB::class))

        manager.destroy()

        manager.fireEvent(EventA)
        manager.fireEvent(EventB)

        verifyNoInteractions(subscriber1, subscriber2)

        verify(out, times(2)).println(anyString())
    }

    @Test
    fun `should add subscribers during event processing`() {
        val manager = DefaultEventManager()
        val subscribers: Array<EventSubscriber> = Array(10) { i ->
            mock {
                on { identity } doReturn i
            }
        }

        for (subscriber in subscribers) {
            manager.addSubscriber(subscriber, arrayOf(EventA::class))
        }

        val newSubscriber: EventSubscriber = mock {
            on { identity } doReturn subscribers.size
        }
        whenever(subscribers[5].notify(EventA)).thenAnswer {
            manager.addSubscriber(newSubscriber, arrayOf(EventA::class))
            manager.addSubscriber(newSubscriber, arrayOf(EventB::class))
        }

        manager.fireEvent(EventA)

        for (subscriber in subscribers) {
            verify(subscriber).notify(EventA)
        }
        verify(newSubscriber, never()).notify(EventA)
        reset(newSubscriber, *subscribers)

        manager.fireEvent(EventA)
        manager.fireEvent(EventB)

        for (subscriber in subscribers) {
            verify(subscriber).notify(EventA)
            verifyNoMoreInteractions(subscriber)
        }
        verify(newSubscriber).notify(EventA)
        verify(newSubscriber).notify(EventB)
        verifyNoMoreInteractions(newSubscriber)
    }

    @Test
    fun `should remove subscribers during event processing`() {
        val manager = DefaultEventManager()
        val subscribers: Array<EventSubscriber> = Array(10) { i ->
            mock {
                on { identity } doReturn i
            }
        }

        for (subscriber in subscribers) {
            manager.addSubscriber(subscriber, arrayOf(EventA::class))
        }

        whenever(subscribers[5].notify(EventA)).thenAnswer {
            manager.removeSubscriber(subscribers.last(), arrayOf(EventA::class))
        }

        manager.fireEvent(EventA)

        for (subscriber in subscribers) {
            verify(subscriber).notify(EventA)
            verify(subscriber, atLeastOnce()).identity
            verifyNoMoreInteractions(subscriber)
        }
        reset(*subscribers)

        manager.fireEvent(EventA)

        for (subscriber in subscribers) {
            if (subscriber == subscribers.last()) {
                verifyNoInteractions(subscriber)
            } else {
                verify(subscriber).notify(EventA)
                verifyNoMoreInteractions(subscriber)
            }
        }
    }
}