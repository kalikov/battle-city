package com.kalikov.game

class PlayerTankController(
    private val eventManager: EventManager,
    private val pauseManager: PauseManager,
    val tank: PlayerTankHandle
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            BaseExplosion.Destroyed::class,
            Keyboard.KeyPressed::class,
            Keyboard.KeyReleased::class
        )
    }

    private var active = true

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        when (event) {
            is Keyboard.KeyPressed -> {
                keyPressed(event.key)
            }

            is Keyboard.KeyReleased -> {
                keyReleased(event.key)
            }

            is BaseExplosion.Destroyed -> {
                tank.isIdle = true
                tank.stopShooting()
                active = false
            }

            else -> Unit
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (!active || pauseManager.isPaused) {
            return
        }
        when (key) {
            Keyboard.Key.LEFT -> {
                tank.direction = Direction.LEFT
                tank.isIdle = false
            }

            Keyboard.Key.RIGHT -> {
                tank.direction = Direction.RIGHT
                tank.isIdle = false
            }

            Keyboard.Key.UP -> {
                tank.direction = Direction.UP
                tank.isIdle = false
            }

            Keyboard.Key.DOWN -> {
                tank.direction = Direction.DOWN
                tank.isIdle = false
            }

            Keyboard.Key.SPACE -> {
                tank.startShooting()
            }

            else -> {
            }
        }
    }

    private fun keyReleased(key: Keyboard.Key) {
        if (tank.direction == Direction.LEFT && key == Keyboard.Key.LEFT ||
            tank.direction == Direction.RIGHT && key == Keyboard.Key.RIGHT ||
            tank.direction == Direction.UP && key == Keyboard.Key.UP ||
            tank.direction == Direction.DOWN && key == Keyboard.Key.DOWN
        ) {
            tank.isIdle = true
        }
        if (key == Keyboard.Key.SPACE) {
            tank.stopShooting()
        }
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}