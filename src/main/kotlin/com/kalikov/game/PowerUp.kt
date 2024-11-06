package com.kalikov.game

class PowerUp(
    private val game: Game,
    position: PixelPoint,
) : Sprite(game.eventManager, position.x, position.y, SIZE_IN_PIXELS, SIZE_IN_PIXELS) {
    companion object {
        val SIZE_IN_TILES = t(2)
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

        z = 500
    }

    override fun draw(surface: ScreenSurface) {
        if (blinkTimer.isOpaque) {
            surface.draw(x, y, image, type.index * width, px(0), width, height)
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
            game.eventManager.fireEvent(Player.Score(tank.player, this.value))
            destroy()
        }
    }

    override fun dispose() {
        LeaksDetector.remove(this)
    }
}