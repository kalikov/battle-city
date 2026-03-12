package com.kalikov.game

import com.kalikov.engine.ScreenSurface

interface BulletsManager {
    fun update()

    fun draw(surface: ScreenSurface)

    fun forEach(action: (Bullet) -> Unit)
    fun iterateWhile(predicate: (Bullet) -> Boolean): Boolean
}