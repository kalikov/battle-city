package com.kalikov.game

import java.time.Clock

class PowerUp(
    private val eventRouter: EventRouter,
    imageManager: ImageManager,
    position: Point,
    clock: Clock
) : Sprite(eventRouter, position.x, position.y, SIZE, SIZE) {
    companion object {
        const val SIZE = Globals.UNIT_SIZE
    }

    data class Destroyed(val powerUp: PowerUp) : Event()
    data class Pick(val powerUp: PowerUp, val tank: Tank) : Event()

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

    private val blinkTimer = BlinkTimer(clock, 128)
    private val image = imageManager.getImage("powerup")

    init {
        LeaksDetector.add(this)

        z = 500
    }

    override fun draw(surface: ScreenSurface) {
        if (blinkTimer.isOpaque) {
            surface.draw(x, y, image, type.index * width, 0, width, height)
        }
    }

    override fun updateHook() {
        if (blinkTimer.isStopped) {
            blinkTimer.restart()
        }
        blinkTimer.update()
    }

    fun pick(tank: Tank) {
        if (!isDestroyed) {
            eventRouter.fireEvent(Pick(this, tank))
            destroy()
        }
    }

    override fun destroyHook() {
        eventRouter.fireEvent(Destroyed(this))
    }

    override fun dispose() {
        LeaksDetector.remove(this)
    }
}