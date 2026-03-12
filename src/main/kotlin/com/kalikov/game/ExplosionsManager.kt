package com.kalikov.game

import com.kalikov.engine.ScreenSurface

interface ExplosionsManager {
    fun draw(surface: ScreenSurface)

    fun update()
}