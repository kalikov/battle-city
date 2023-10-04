package com.kalikov.game

import java.time.Clock
import kotlin.random.Random

class PowerUpFactory(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val spriteContainer: SpriteContainer,
    private val clock: Clock,
    private val bounds: Rect,
    private val random: Random = Random.Default,
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(Tank.FlashingTankHit::class, EnemyFactory.EnemyCreated::class)
    }

    private var powerUp: PowerUp? = null

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is Tank.FlashingTankHit) {
            powerUp?.destroy()
            val newPowerUp = create()
            spriteContainer.addSprite(newPowerUp)
            powerUp = newPowerUp
        } else if (event is EnemyFactory.EnemyCreated) {
            if (event.enemy.isFlashing) {
                powerUp?.destroy()
            }
        }
    }

    private fun create(): PowerUp {
        val position = Point(
            bounds.x + random.nextInt(bounds.width - PowerUp.SIZE),
            bounds.y + random.nextInt(bounds.height - PowerUp.SIZE),
        )
        val powerUp = PowerUp(eventManager, imageManager, position, clock)
        powerUp.type = PowerUp.Type.entries.random()

        eventManager.fireEvent(SoundManager.Play("powerup_appear"))

        return powerUp
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}