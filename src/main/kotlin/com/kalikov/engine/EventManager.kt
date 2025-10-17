package com.kalikov.engine

import kotlin.reflect.KClass

interface EventManager : EventRouter {
    fun addSubscriber(subscriber: EventSubscriber, events: Array<KClass<out Event>>)

    fun removeSubscriber(subscriber: EventSubscriber, events: Array<KClass<out Event>>)
}