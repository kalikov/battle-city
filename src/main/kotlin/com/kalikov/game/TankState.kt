package com.kalikov.game

import com.kalikov.engine.ScreenSurface

interface TankState {
    val canShoot: Boolean
    val canMove: Boolean
    val canBeDestroyed: Boolean

    val isCollidable: Boolean

    fun update()

    fun draw(surface: ScreenSurface)

    fun dispose()
}