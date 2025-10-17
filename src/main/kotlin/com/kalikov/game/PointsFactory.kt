package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber

class PointsFactory(
    private val game: BattleCityGame,
    private val pauseManager: PauseManager,
    private val spriteContainer: SpriteContainer,
) : EventSubscriber {
    override val identity: Int
        get() = TODO("Not yet implemented")

    private companion object {
        private val subscriptions = arrayOf(TankExplosion.Destroyed::class, PowerUp.Pick::class)
    }

    init {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is TankExplosion.Destroyed && event.explosion.tank is EnemyTank) {
            val explosion = event.explosion
            val tank = event.explosion.tank
            if (tank.value > 0) {
                spriteContainer.addSprite(create(explosion, tank.value, 200))
            }
        } else if (event is PowerUp.Pick) {
            val powerUp = event.powerUp
            spriteContainer.addSprite(create(powerUp, powerUp.value, 800))
        }
    }

    private fun create(parent: Sprite, value: Int, duration: Int): Points {
        return Points(
            game,
            pauseManager,
            value,
            parent.center - Points.SIZE / 2,
            parent.middle - Points.SIZE / 2,
            duration
        )
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}