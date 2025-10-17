package com.kalikov.engine

interface Timer {
    val isStopped: Boolean

    fun restart()

    fun update()

    fun stop()
}