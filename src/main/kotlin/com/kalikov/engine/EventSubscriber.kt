package com.kalikov.engine

interface EventSubscriber {
    val identity: Int

    fun notify(event: Event)
}