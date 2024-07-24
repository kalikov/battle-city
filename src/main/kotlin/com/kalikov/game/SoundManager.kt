package com.kalikov.game

interface SoundManager {
    data class Play(val name: String): Event()
    data class Stop(val name: String): Event()
    data object Pause: Event()
    data object Resume: Event()

    fun load(name: String, path: String)

    fun destroy()
}