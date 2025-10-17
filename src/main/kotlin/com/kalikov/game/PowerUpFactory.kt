package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import kotlin.random.Random

class PowerUpFactory(
    private val game: BattleCityGame,
    private val spriteContainer: SpriteContainer,
    private val bounds: PixelRect,
    private val random: Random = Random.Default,
) : EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf(EnemyFactory.FlashingTankHit::class, EnemyFactory.EnemyCreated::class)

        private val appearTable = arrayOf(
            PowerUp.Type.HELMET,
            PowerUp.Type.TIMER,
            PowerUp.Type.SHOVEL,
            PowerUp.Type.STAR,
            PowerUp.Type.GRENADE,
            PowerUp.Type.TANK,
            PowerUp.Type.GRENADE,
            PowerUp.Type.STAR
        )
    }

    data class PowerUpCreated(val powerUp: PowerUp) : Event()

    private var powerUp: PowerUp? = null
    override val identity: Int
        get() = TODO("Not yet implemented")

    init {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is EnemyFactory.FlashingTankHit) {
            powerUp?.destroy()
            val newPowerUp = create()
            spriteContainer.addSprite(newPowerUp)
            powerUp = newPowerUp
            game.eventManager.fireEvent(PowerUpCreated(newPowerUp))
        } else if (event is EnemyFactory.EnemyCreated) {
            if (event.isFlashing) {
                powerUp?.destroy()
            }
        }
    }

    private fun create(): PowerUp {
        val position = PixelPoint(
            bounds.x + Globals.TILE_SIZE * random.nextInt((GameField.SIZE_IN_TILES - PowerUp.SIZE_IN_TILES).toInt()),
            bounds.y + Globals.TILE_SIZE * random.nextInt((GameField.SIZE_IN_TILES - PowerUp.SIZE_IN_TILES).toInt())
        )
        val powerUp = PowerUp(game, position)
        powerUp.type = appearTable.random(random)

        game.soundManager.powerUpAppear.play()

        return powerUp
    }

    fun dispose() {
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}