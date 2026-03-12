package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.LeaksDetector
import com.kalikov.engine.ScreenSurface
import com.kalikov.util.ArraySet

class GameExplosionsManager(
    private val game: BattleCityGame,
    private val pauseManager: PauseManager,
) : ExplosionsManager, EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf(
            Bullet.Exploded::class,
            Base.Hit::class,
            Tank.Destroyed::class
        )
    }

    override val identity get() = Globals.IDENTITY_EXPLOSION_MANAGER

    private val explosions = ArraySet<Explosion>(Sprite.ID_ORDER)

    fun activate() {
        LeaksDetector.add(this)

        game.eventManager.addSubscriber(this, subscriptions)
    }

    fun deactivate() {
        game.eventManager.removeSubscriber(this, subscriptions)

        explosions.forEach { it.dispose() }
        explosions.clear()

        LeaksDetector.remove(this)
    }

    override fun draw(surface: ScreenSurface) {
        explosions.forEach {
            if (!it.isDestroyed) {
                it.draw(surface)
            }
        }
    }

    override fun update() {
        explosions.removeIf {
            if (it.isDestroyed) {
                when (it) {
                    is BulletExplosion -> {
                        it.bullet.tank.reload()
                    }

                    is TankExplosion -> {
                        game.eventManager.fireEvent(TankExplosion.Destroyed(it))
                    }

                    is BaseExplosion -> {
                        game.eventManager.fireEvent(BaseExplosion.Destroyed(it))
                    }
                }
            }
            it.update()
        }
    }

    override fun notify(event: Event) {
        if (event is Bullet.Exploded) {
            explosions.add(create(event.bullet))
        }
        if (event is Base.Hit) {
            explosions.add(create(event.base))
        }
        if (event is Tank.Destroyed) {
            explosions.add(create(event.tank))
        }
    }

    private fun create(bullet: BulletHandle): BulletExplosion {
        return BulletExplosion(game, pauseManager, bullet)
    }

    private fun create(base: BaseHandle): BaseExplosion {
        val explosion = BaseExplosion(
            game,
            pauseManager,
            base.x + Base.SIZE / 2 - BaseExplosion.SIZE / 2,
            base.y + Base.SIZE / 2 - BaseExplosion.SIZE / 2,
        )
        game.soundManager.playerExplosion.play()

        return explosion
    }

    private fun create(tank: Tank): TankExplosion {
        val explosion = TankExplosion(game, pauseManager, tank)
        explosion.setPosition(tank.center - explosion.width / 2, tank.middle - explosion.height / 2)

        return explosion
    }

    fun dispose() {
        explosions.forEach { it.dispose() }
        explosions.clear()
    }
}