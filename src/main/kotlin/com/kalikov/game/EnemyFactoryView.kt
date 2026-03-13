package com.kalikov.game

import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface

class EnemyFactoryView(
    private val imageManager: ImageManager,
    private val enemyFactory: GameEnemyTanksManager,
    private val x: Pixel,
    private val y: Pixel
) {
    fun draw(surface: ScreenSurface) {
        val image = imageManager.getImage("enemy")
        for (i in 0 until enemyFactory.enemiesToCreateCount) {
            val col = i % 2
            val row = i / 2
            surface.draw(x + col.tiles.toPixel(), y + row.tiles.toPixel(), image)
        }
    }
}