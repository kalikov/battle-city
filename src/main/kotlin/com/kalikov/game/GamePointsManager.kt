package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.ScreenSurface
import com.kalikov.util.ArraySet

class GamePointsManager(
    private val game: BattleCityGame,
    private val pauseManager: PauseManager,
) : PointsManager, EventSubscriber {
    override val identity get() = Globals.IDENTITY_POINTS_MANAGER

    private companion object {
        private val subscriptions = arrayOf(TankExplosion.Destroyed::class, PowerUp.Pick::class)
    }

    private val points = ArraySet<Points>(Sprite.ID_ORDER)

    fun activate() {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    fun deactivate() {
        game.eventManager.removeSubscriber(this, subscriptions)

        points.forEach { it.dispose() }
        points.clear()
    }

    override fun update() {
        points.removeIf {
            it.update()
        }
    }

    override fun draw(surface: ScreenSurface) {
        points.forEach {
            if (!it.isDestroyed) {
                it.draw(surface)
            }
        }
    }

    override fun notify(event: Event) {
        if (event is TankExplosion.Destroyed && event.explosion.tank is EnemyTank) {
            val explosion = event.explosion
            val tank = event.explosion.tank
            if (tank.value > 0) {
                create(explosion, tank.value, 200)
            }
        } else if (event is PowerUp.Pick) {
            val powerUp = event.powerUp
            create(powerUp, powerUp.value, 800)
        }
    }

    private fun create(parent: AbstractSprite, value: Int, duration: Int) {
        points.add(
            Points(
                game,
                pauseManager,
                value,
                parent.center - Points.SIZE / 2,
                parent.middle - Points.SIZE / 2,
                duration
            )
        )
    }

    fun dispose() {
        points.forEach { it.dispose() }
        points.clear()
    }
}