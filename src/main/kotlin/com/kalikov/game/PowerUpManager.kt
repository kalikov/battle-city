package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.ScreenSurface

interface PowerUpManager {
    data class PowerUpCreated(val powerUp: PowerUp) : Event()
    data class PowerUpDestroyed(val powerUp: PowerUp) : Event()

    data object Freeze : Event()
    data object ShovelStart : Event()

    val powerUp: PowerUp?
    fun activate()
    fun deactivate()
    fun dispose()
    fun draw(surface: ScreenSurface)
}