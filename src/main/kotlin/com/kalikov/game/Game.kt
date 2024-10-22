package com.kalikov.game

import java.time.Clock

interface Game {
    val config: GameConfig

    val clock: Clock

    val screen: Screen

    val eventManager: EventManager
    val imageManager: ImageManager
    val soundManager: SoundManager
}