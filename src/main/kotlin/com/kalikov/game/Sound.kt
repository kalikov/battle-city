package com.kalikov.game

interface Sound {
    enum class State {
        STOPPED,
        PLAYING,
        PAUSED
    }

    val state: State

    fun play()

    fun loop()

    fun pause()

    fun resume()

    fun stop()
}
