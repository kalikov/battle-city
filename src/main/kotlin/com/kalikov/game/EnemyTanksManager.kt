package com.kalikov.game

import com.kalikov.engine.ScreenSurface

interface EnemyTanksManager {
    fun update()

    fun draw(surface: ScreenSurface)

    fun forEach(action: (EnemyTank) -> Unit)

    fun iterateWhile(predicate: (EnemyTank) -> Boolean) : Boolean
}