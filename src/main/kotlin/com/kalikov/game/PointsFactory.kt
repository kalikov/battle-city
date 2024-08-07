package com.kalikov.game

import java.time.Clock

class PointsFactory(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val spriteContainer: SpriteContainer,
    private val clock: Clock
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(TankExplosion.Destroyed::class, PowerUp.Pick::class)
    }

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is TankExplosion.Destroyed && event.explosion.tank is EnemyTank) {
            val explosion = event.explosion
            val tank = event.explosion.tank
            if (tank.value > 0) {
                spriteContainer.addSprite(create(explosion.center, tank.value, 200))
            }
        } else if (event is PowerUp.Pick) {
            val powerUp = event.powerUp
            spriteContainer.addSprite(create(powerUp.center, powerUp.value, 800))
        }
    }

    private fun create(center: Point, value: Int, duration: Int): Points {
        return Points(
            eventManager,
            imageManager,
            clock,
            value,
            center.x - Points.SIZE / 2,
            center.y - Points.SIZE / 2,
            duration
        )
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}