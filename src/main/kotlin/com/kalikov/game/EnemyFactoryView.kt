package com.kalikov.game

class EnemyFactoryView(
    private val imageManager: ImageManager,
    private val enemyFactory: EnemyFactory,
    private val x: Pixel,
    private val y: Pixel
) {
    fun draw(surface: ScreenSurface) {
        val image = imageManager.getImage("enemy")
        for (i in 0 until enemyFactory.enemiesToCreateCount) {
            val col = i % 2
            val row = i / 2
            surface.draw(x + t(col).toPixel(), y + t(row).toPixel(), image)
        }
    }
}