package com.kalikov.game

import com.kalikov.engine.ScreenSurface

interface PlayerTanksManager {
    fun update()

    fun draw(surface: ScreenSurface)

    fun getTank(player: Player): PlayerTankHandle?

    fun forEach(action: (PlayerTank) -> Unit)

    fun iterateWhile(predicate: (PlayerTank) -> Boolean) : Boolean
}