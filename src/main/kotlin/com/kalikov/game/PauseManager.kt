package com.kalikov.game

import com.kalikov.engine.Event

interface PauseManager {
    data object Start : Event()
    data object End : Event()

    val isPaused: Boolean
}