package com.kalikov.game

interface SoundManager {
    data class Play(val name: String): Event()
    data class Loop(val name: String): Event()
    data class Stop(val name: String): Event()
    data object Pause: Event()
    data object Resume: Event()

    fun isPlaying(name: String): Boolean

    var enabled: Boolean
}