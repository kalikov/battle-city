package com.kalikov.game

import java.time.Clock

class PointsFactory(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val spriteContainer: SpriteContainer,
    private val clock: Clock
) : EventSubscriber {
    data class PointsCreated(val points: Points) : Event()

    private companion object {
        private val subscriptions = setOf(TankExplosion.Destroyed::class, PowerUp.Pick::class)
    }

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is TankExplosion.Destroyed && enemyTankExplosionEnd(event)) {
            val explosion = event.explosion
            val tank = explosion.tank
            spriteContainer.addSprite(create(explosion.center, tank.value, Points.Type.TANK, 200))
        } else if (event is PowerUp.Pick) {
            val powerUp = event.powerUp
            spriteContainer.addSprite(create(powerUp.center, powerUp.value, Points.Type.POWERUP, 800))
        }
    }

    private fun create(center: Point, value: Int, type: Points.Type, duration: Int): Points {
        val points = Points(
            eventManager,
            imageManager,
            clock,
            center.x - Points.SIZE / 2,
            center.y - Points.SIZE / 2,
            duration
        )
        points.value = value
        points.type = type
        eventManager.fireEvent(PointsCreated(points))
        return points
    }

    private fun enemyTankExplosionEnd(event: TankExplosion.Destroyed): Boolean {
        val tank = event.explosion.tank
        if (!tank.isEnemy) {
            return false
        }
        if (tank.value <= 0) {
            return false
        }
        return true
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}