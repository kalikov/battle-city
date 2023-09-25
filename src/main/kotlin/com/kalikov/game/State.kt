package com.kalikov.game

interface State {
    fun deactivate()
    fun dispose()
    fun activate()
    fun think()
    fun isTransparent(): Boolean
    fun blit()
}