package com.kalikov.engine

import com.kalikov.util.ArraySet
import java.io.PrintStream
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

open class DefaultEventManager(private val out: PrintStream = System.out) : EventManager {
    private val comparator = object : Comparator<EventSubscriber> {
//        private val map = WeakIdentityToIntMap<EventSubscriber>()

//        private var counter = 0

        override fun compare(o1: EventSubscriber, o2: EventSubscriber): Int {
//            val id1 = map.computeIfAbsent(o1) { ++counter }
//            val id2 = map.computeIfAbsent(o2) { ++counter }
            val id1 = o1.identity
            val id2 = o2.identity
            return id1 - id2
        }
    }

    private val subscriptions: MutableMap<KClass<out Event>, Entry> = ConcurrentHashMap()

    override fun addSubscriber(subscriber: EventSubscriber, events: Array<KClass<out Event>>) {
        events.forEach { event ->
            val subscribers = subscriptions.computeIfAbsent(event) { Entry() }
            subscribers.add(subscriber)
        }
    }

    override fun removeSubscriber(subscriber: EventSubscriber, events: Array<KClass<out Event>>) {
        events.forEach { event ->
            subscriptions[event]?.remove(subscriber)
        }
    }

    override fun fireEvent(event: Event) {
        val subscribers = subscriptions[event::class] ?: return
        subscribers.forEach { subscriber ->
            subscriber.notify(event)
        }
    }

    fun destroy() {
        subscriptions.forEach { (event, subscribers) ->
            subscribers.forEach { subscriber -> out.println("Dangling subscriber $subscriber for event ${event.qualifiedName}") }
        }
        subscriptions.clear()
    }

    private inner class Entry {
        private var set = ArraySet(comparator)
        private var readSet: ArraySet<EventSubscriber>? = null

        fun add(subscriber: EventSubscriber) {
            if (readSet === set) {
                set = ArraySet(set)
            }
            require(set.add(subscriber))
        }

        fun remove(subscriber: EventSubscriber) {
            if (readSet === set) {
                set = ArraySet(set)
            }
            require(set.remove(subscriber)) { "Failed to remove subscriber $subscriber" }
        }

        fun forEach(callback: (EventSubscriber) -> Unit) {
            val setRef = set
            if (readSet === setRef) {
                setRef.forEach(callback)
            } else {
                readSet = setRef
                try {
                    setRef.forEach(callback)
                } finally {
                    if (readSet === setRef) {
                        readSet = null
                    }
                }
            }
        }
    }
}