package com.kalikov.game

class PlayerTankController(
    private val eventManager: EventManager,
    private val pauseManager: PauseManager,
    val tank: PlayerTankHandle,
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            BaseExplosion.Destroyed::class,
            Keyboard.KeyPressed::class,
            Keyboard.KeyReleased::class
        )

        private const val FLAG_LEFT = 1
        private const val FLAG_RIGHT = 2
        private const val FLAG_UP = 1
        private const val FLAG_DOWN = 2
        private const val FLAG_BOTH = 3
    }

    private var isActive = true

    private var horzPressed: Int = 0
    private var vertPressed: Int = 0

    private var targetDirection: Direction? = null

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    fun update() {
        targetDirection?.let {
            setDirection(it)
        }
    }

    override fun notify(event: Event) {
        when (event) {
            is Keyboard.KeyPressed -> {
                if (event.playerIndex == tank.player.index) {
                    keyPressed(event.key)
                }
            }

            is Keyboard.KeyReleased -> {
                if (event.playerIndex == tank.player.index) {
                    keyReleased(event.key)
                }
            }

            is BaseExplosion.Destroyed -> {
                tank.isIdle = true
                tank.stopShooting()
                targetDirection = null
                isActive = false
            }

            else -> Unit
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (!isActive || pauseManager.isPaused) {
            return
        }
        when (key) {
            Keyboard.Key.LEFT -> {
                horzPressed = horzPressed or FLAG_LEFT
                if (horzPressed == FLAG_BOTH) {
                    updateStateOnVert()
                } else {
                    setDirection(Direction.LEFT)
                    tank.isIdle = false
                }
            }

            Keyboard.Key.RIGHT -> {
                horzPressed = horzPressed or FLAG_RIGHT
                if (horzPressed == FLAG_BOTH) {
                    updateStateOnVert()
                } else {
                    setDirection(Direction.RIGHT)
                    tank.isIdle = false
                }
            }

            Keyboard.Key.UP -> {
                vertPressed = vertPressed or FLAG_UP
                if (vertPressed == FLAG_BOTH) {
                    updateStateOnHorz()
                } else {
                    setDirection(Direction.UP)
                    tank.isIdle = false
                }
            }

            Keyboard.Key.DOWN -> {
                vertPressed = vertPressed or FLAG_DOWN
                if (vertPressed == FLAG_BOTH) {
                    updateStateOnHorz()
                } else {
                    setDirection(Direction.DOWN)
                    tank.isIdle = false
                }
            }

            Keyboard.Key.ACTION -> {
                tank.startShooting()
            }

            else -> {
            }
        }
    }

    private fun updateStateOnHorz() {
        if (horzPressed != 0 && horzPressed != FLAG_BOTH) {
            setDirection(if (horzPressed == FLAG_LEFT) Direction.LEFT else Direction.RIGHT)
            tank.isIdle = false
        } else {
            tank.isIdle = true
            targetDirection = null
        }
    }

    private fun updateStateOnVert() {
        if (vertPressed != 0 && vertPressed != FLAG_BOTH) {
            setDirection(if (vertPressed == FLAG_UP) Direction.UP else Direction.DOWN)
            tank.isIdle = false
        } else {
            tank.isIdle = true
            targetDirection = null
        }
    }

    private fun setDirection(direction: Direction) {
        tank.direction = direction
        targetDirection = if (tank.direction == direction) {
            null
        } else {
            direction
        }
    }

    private fun keyReleased(key: Keyboard.Key) {
        if (key == Keyboard.Key.LEFT) {
            horzPressed = horzPressed and FLAG_LEFT.inv()
            if (horzPressed == 0) {
                updateStateOnVert()
            } else {
                updateStateOnHorz()
            }
        }
        if (key == Keyboard.Key.RIGHT) {
            horzPressed = horzPressed and FLAG_RIGHT.inv()
            if (horzPressed == 0) {
                updateStateOnVert()
            } else {
                updateStateOnHorz()
            }
        }
        if (key == Keyboard.Key.UP) {
            vertPressed = vertPressed and FLAG_UP.inv()
            if (vertPressed == 0) {
                updateStateOnHorz()
            } else {
                updateStateOnVert()
            }
        }
        if (key == Keyboard.Key.DOWN) {
            vertPressed = vertPressed and FLAG_DOWN.inv()
            if (vertPressed == 0) {
                updateStateOnHorz()
            } else {
                updateStateOnVert()
            }
        }
        if (key == Keyboard.Key.ACTION) {
            tank.stopShooting()
        }
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}