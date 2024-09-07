package com.kalikov.game

interface Game {
    val config: GameConfig

    val screen: Screen

    val eventManager: EventManager
    val imageManager: ImageManager
}