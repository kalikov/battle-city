package com.kalikov.game

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

class ConcurrentEventManager : EventManager {
    private val subscriptions: MutableMap<KClass<out Event>, MutableList<EventSubscriber>> = ConcurrentHashMap()

    override fun addSubscriber(subscriber: EventSubscriber, events: Set<KClass<out Event>>) {
        for (event in events) {
            val subscribers = subscriptions.computeIfAbsent(event) { CopyOnWriteArrayList() }
            subscribers.add(subscriber)
        }
    }

    override fun removeSubscriber(subscriber: EventSubscriber, events: Set<KClass<out Event>>) {
        for (event in events) {
            subscriptions[event]?.remove(subscriber)
        }
    }

    override fun fireEvent(event: Event) {
        val subscribers = subscriptions[event::class] ?: return
        for (subscriber in subscribers) {
            subscriber.notify(event)
        }
    }

    fun destroy() {
        subscriptions.forEach { (event, subscribers) ->
            subscribers.forEach { subscriber -> println("Dangling subscriber $subscriber for event ${event.qualifiedName}") }
        }
        subscriptions.clear()
    }
}