package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.LeaksDetector
import kotlin.reflect.KClass

class TankExplosionFactory(
    private val game: BattleCityGame,
    private val pauseManager: PauseManager,
    private val spriteContainer: SpriteContainer
) : EventSubscriber {
    override val identity: Int
        get() = TODO("Not yet implemented")

    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(Tank.Destroyed::class)
    }

    init {
        LeaksDetector.add(this)

        game.eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is Tank.Destroyed) {
            spriteContainer.addSprite(create(event.tank))
        }
    }

    private fun create(tank: Tank): TankExplosion {
        val explosion = TankExplosion(game, pauseManager, tank)
        explosion.setPosition(tank.center - explosion.width / 2, tank.middle - explosion.height / 2)

        return explosion
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}