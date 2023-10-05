package com.kalikov.game

interface Sound {
    val isPlaying: Boolean

    fun play()

    fun loop()

    fun stop()

    fun dispose()
}
