package com.kalikov.game

interface SoundManager {
    fun isPlaying(name: String): Boolean

    fun play(name: String)
    fun loop(name: String)
    fun stop(name: String)

    fun pauseAll()
    fun resumeAll()

    var enabled: Boolean
}