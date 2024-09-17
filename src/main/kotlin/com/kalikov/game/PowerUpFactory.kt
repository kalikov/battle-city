package com.kalikov.game

import kotlin.random.Random

class PowerUpFactory(
    private val game: Game,
    private val spriteContainer: SpriteContainer,
    private val bounds: PixelRect,
    private val random: Random = Random.Default,
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(EnemyFactory.FlashingTankHit::class, EnemyFactory.EnemyCreated::class)
    }

    private var powerUp: PowerUp? = null

    init {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is EnemyFactory.FlashingTankHit) {
            powerUp?.destroy()
            val newPowerUp = create()
            spriteContainer.addSprite(newPowerUp)
            powerUp = newPowerUp
        } else if (event is EnemyFactory.EnemyCreated) {
            if (event.isFlashing) {
                powerUp?.destroy()
            }
        }
    }

    private fun create(): PowerUp {
        val position = PixelPoint(
            bounds.x + random.nextInt((bounds.width - PowerUp.SIZE).toInt()),
            bounds.y + random.nextInt((bounds.height - PowerUp.SIZE).toInt()),
        )
        val powerUp = PowerUp(game, position)
        powerUp.type = PowerUp.Type.entries.random()

        game.eventManager.fireEvent(SoundManager.Play("powerup_appear"))

        return powerUp
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}