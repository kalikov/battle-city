package com.kalikov.game

interface EventSubscriber {
    fun notify(event: Event)
}