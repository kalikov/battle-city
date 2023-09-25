package com.kalikov.game

interface PauseManager {
    data object Start : Event()
    data object End : Event()

    val isPaused: Boolean
}