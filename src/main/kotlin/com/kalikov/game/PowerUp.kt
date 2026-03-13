package com.kalikov.game

import com.kalikov.engine.BlinkTimer
import com.kalikov.engine.Event
import com.kalikov.engine.LeaksDetector
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import com.kalikov.engine.times

class PowerUp(
    private val game: BattleCityGame,
    position: PixelPoint,
) : AbstractSprite(position.x, position.y, SIZE_IN_PIXELS, SIZE_IN_PIXELS) {
    companion object {
        val SIZE_IN_TILES = 2.tiles
        val SIZE_IN_PIXELS = SIZE_IN_TILES.toPixel()
    }

    data class Pick(val powerUp: PowerUp, val tank: PlayerTank) : Event()

    enum class Type(val key: String, val index: Int) {
        HELMET("helmet", 0),
        TIMER("timer", 1),
        SHOVEL("shovel", 2),
        STAR("star", 3),
        GRENADE("grenade", 4),
        TANK("tank", 5)
    }

    var type = Type.GRENADE
    var value = 500

    private val blinkTimer = BlinkTimer(game.clock, 128)
    private val image = game.imageManager.getImage("powerup")

    init {
        LeaksDetector.add(this)
    }

    override fun draw(surface: ScreenSurface) {
        if (blinkTimer.isOpaque) {
            surface.draw(x, y, image, type.index * width, 0.px, width, height)
        }
    }

    override fun updateHook() {
        if (blinkTimer.isStopped) {
            blinkTimer.restart()
        }
        blinkTimer.update()
    }

    fun pick(tank: PlayerTank) {
        if (!isDestroyed) {
            game.eventManager.fireEvent(Pick(this, tank))
            tank.player.score(value)
            destroy()
        }
    }

    override fun dispose() {
        LeaksDetector.remove(this)
    }
}