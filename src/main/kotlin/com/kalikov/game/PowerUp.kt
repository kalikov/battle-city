package com.kalikov.game

import java.time.Clock

class PowerUp(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    position: Point,
    clock: Clock
) : Sprite(eventManager, position.x, position.y, Globals.UNIT_SIZE, Globals.UNIT_SIZE), EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            EnemyFactory.EnemyCreated::class
        )
    }

    data class Destroyed(val powerUp: PowerUp) : Event()
    data class Pick(val powerUp: PowerUp, val tank: Tank) : Event()

    enum class Type(val key: String, val index: Int) {
        GRENADE("grenade", 4),
        HELMET("helmet", 0),
        SHOVEL("shovel", 2),
        STAR("star", 3),
        TANK("tank", 4),
        TIMER("timer", 1)
    }

    var type = Type.GRENADE
    var value = 500

    private val blinkTimer = BlinkTimer(clock, 200)
    private val image = imageManager.getImage("powerup")

    init {
        LeaksDetector.add(this)

        z = 500

        eventManager.addSubscriber(this, subscriptions)
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
            eventManager.fireEvent(Pick(this, tank))
            destroy()
        }
    }

    override fun notify(event: Event) {
        if (event is EnemyFactory.EnemyCreated) {
            if (event.enemy.isFlashing) {
                destroy()
            }
        }
    }

    override fun destroyHook() {
        eventManager.fireEvent(Destroyed(this))
    }

    override fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}