package com.kalikov.game

import com.kalikov.engine.ScreenSurface

interface GameFieldLayer {
    fun draw(surface: ScreenSurface)
}