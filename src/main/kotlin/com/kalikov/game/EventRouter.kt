package com.kalikov.game

interface EventRouter {
    fun fireEvent(event: Event)
}