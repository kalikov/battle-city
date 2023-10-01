package com.kalikov.game

import java.time.Clock

class PowerUpFactory(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val spriteContainer: SpriteContainer,
    private val clock: Clock
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(Tank.FlashingTankHit::class, EnemyFactory.EnemyCreated::class)
    }

    private var positions: Array<Point>

    private var powerUp: PowerUp? = null

    init {
        eventManager.addSubscriber(this, subscriptions)

        val powerUpCol1X = Globals.UNIT_SIZE + 15
        val powerUpCol2X = 4 * Globals.UNIT_SIZE + 15
        val powerUpCol3X = 7 * Globals.UNIT_SIZE + 15
        val powerUpCol4X = 10 * Globals.UNIT_SIZE + 15

        val powerUpRow1Y = Globals.UNIT_SIZE + 17
        val powerUpRow2Y = 4 * Globals.UNIT_SIZE + 17
        val powerUpRow3Y = 7 * Globals.UNIT_SIZE + 17
        val powerUpRow4Y = 10 * Globals.UNIT_SIZE + 17

        positions = arrayOf(
            Point(powerUpCol1X, powerUpRow1Y),
            Point(powerUpCol2X, powerUpRow1Y),
            Point(powerUpCol3X, powerUpRow1Y),
            Point(powerUpCol4X, powerUpRow1Y),

            Point(powerUpCol1X, powerUpRow2Y),
            Point(powerUpCol2X, powerUpRow2Y),
            Point(powerUpCol3X, powerUpRow2Y),
            Point(powerUpCol4X, powerUpRow2Y),

            Point(powerUpCol1X, powerUpRow3Y),
            Point(powerUpCol2X, powerUpRow3Y),
            Point(powerUpCol3X, powerUpRow3Y),
            Point(powerUpCol4X, powerUpRow3Y),

            Point(powerUpCol1X, powerUpRow4Y),
            Point(powerUpCol2X, powerUpRow4Y),
            Point(powerUpCol3X, powerUpRow4Y),
            Point(powerUpCol4X, powerUpRow4Y),
        )
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
        val powerUp = PowerUp(eventManager, imageManager, positions.random(), clock)
        powerUp.type = PowerUp.Type.entries.random()

        eventManager.fireEvent(SoundManager.Play("powerup_appear"))

        return powerUp
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}