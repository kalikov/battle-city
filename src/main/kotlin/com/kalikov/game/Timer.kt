package com.kalikov.game

interface Timer {
    val isStopped: Boolean

    fun restart()

    fun update()
}