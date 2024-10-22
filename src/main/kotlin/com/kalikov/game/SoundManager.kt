package com.kalikov.game

interface SoundManager {
    data class Play(val name: String): Event()
    data class Stop(val name: String): Event()
    data object Pause: Event()
    data object Resume: Event()

    var enabled: Boolean
}