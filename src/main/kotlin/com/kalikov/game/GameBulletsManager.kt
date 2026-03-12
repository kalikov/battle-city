package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.ScreenSurface
import com.kalikov.game.Bullet.Exploded
import com.kalikov.util.ArraySet
import kotlin.reflect.KClass

class GameBulletsManager(
    private val game: BattleCityGame,
) : BulletsManager, EventSubscriber {
    override val identity get() = Globals.IDENTITY_BULLETS_MANAGER

    private val bullets = ArraySet<Bullet>(Sprite.ID_ORDER)

    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(Tank.Shoot::class)
    }

    fun activate() {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    fun deactivate() {
        game.eventManager.removeSubscriber(this, subscriptions)

        bullets.forEach { it.dispose() }
        bullets.clear()
    }

    override fun notify(event: Event) {
        if (event is Tank.Shoot) {
            if (event.bullet.tank is PlayerTank) {
                game.soundManager.bulletShot.play()
            }
            bullets.add(event.bullet)
        }
    }

    override fun update() {
        bullets.removeIf {
            if (it.isDestroyed) {
                if (it.shouldExplode) {
                    game.eventManager.fireEvent(it.explodedEvent)
                } else {
                    it.tank.reload()
                }
            }
            it.update()
        }
    }

    override fun draw(surface: ScreenSurface) {
        bullets.forEach {
            if (!it.isDestroyed) {
                it.draw(surface)
            }
        }
    }

    override fun forEach(action: (Bullet) -> Unit) {
        bullets.forEach(action)
    }

    override fun iterateWhile(predicate: (Bullet) -> Boolean): Boolean {
        return bullets.iterateWhile(predicate)
    }

    fun dispose() {
        bullets.forEach { it.dispose() }
        bullets.clear()
    }
}