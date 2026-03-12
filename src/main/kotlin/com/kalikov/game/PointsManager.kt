package com.kalikov.game

import com.kalikov.engine.ScreenSurface

interface PointsManager {
    fun draw(surface: ScreenSurface)

    fun update()
}