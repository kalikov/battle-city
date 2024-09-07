package com.kalikov.game

import kotlin.reflect.KClass

interface EventManager : EventRouter {
    fun addSubscriber(subscriber: EventSubscriber, events: Set<KClass<out Event>>)

    fun removeSubscriber(subscriber: EventSubscriber, events: Set<KClass<out Event>>)
}