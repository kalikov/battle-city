package com.kalikov.game

interface Music {
    enum class State {
        STOPPED,
        PLAYING,
        PAUSED
    }

    val state: State

    fun play()

    fun stop()

    fun loop()
}

val Music.isPlaying get() = this.state != Music.State.STOPPED