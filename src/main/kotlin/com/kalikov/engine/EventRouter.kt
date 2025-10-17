package com.kalikov.engine

interface EventRouter {
    fun fireEvent(event: Event)
}