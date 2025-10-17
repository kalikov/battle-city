package com.kalikov.game

import com.kalikov.engine.ScreenSurface

interface ImageManager {
    fun getImage(name: String): ScreenSurface
}