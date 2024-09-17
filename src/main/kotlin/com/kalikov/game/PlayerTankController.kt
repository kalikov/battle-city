package com.kalikov.game

class PlayerTankController(
    private val eventManager: EventManager,
    private val pauseManager: PauseManager,
    val player: Player,
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            PlayerTankFactory.PlayerTankCreated::class,
            BaseExplosion.Destroyed::class,
            Keyboard.KeyPressed::class,
            Keyboard.KeyReleased::class,
        )

        private const val FLAG_LEFT = 1
        private const val FLAG_RIGHT = 2
        private const val FLAG_UP = 1
        private const val FLAG_DOWN = 2
        private const val FLAG_BOTH = 3
    }

    var tank: PlayerTankHandle? = null
        private set

    private var isActive = true

    private var horzPressed: Int = 0
    private var vertPressed: Int = 0

    private var targetDirection: Direction? = null

    init {
        LeaksDetector.add(this)

        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        when (event) {
            is PlayerTankFactory.PlayerTankCreated -> {
                if (event.tank.player === player) {
                    tank = event.tank
                }
            }

            is Keyboard.KeyPressed -> {
                tank?.let {
                    if (event.playerIndex == it.player.index) {
                        keyPressed(it, event.key)
                    }
                }
            }

            is Keyboard.KeyReleased -> {
                tank?.let {
                    if (event.playerIndex == it.player.index) {
                        keyReleased(it, event.key)
                    }
                }
            }

            is BaseExplosion.Destroyed -> {
                tank?.let {
                    it.isIdle = true
                    it.stopShooting()
                }
                targetDirection = null
                isActive = false
            }

            else -> Unit
        }
    }

    fun update() {
        tank?.let {
            if (isActive) {
                targetDirection?.let { direction ->
                    if (it.canMove) {
                        setDirection(it, direction)
                    }
                }
                if (it.isIdle && it.canMove && (horzPressed != 0 || vertPressed != 0) && (horzPressed != FLAG_BOTH || vertPressed != FLAG_BOTH)) {
                    it.isIdle = false
                }
            }
        }
    }

    private fun keyPressed(target: PlayerTankHandle, key: Keyboard.Key) {
        if (!isActive || pauseManager.isPaused) {
            return
        }
        when (key) {
            Keyboard.Key.LEFT -> {
                horzPressed = horzPressed or FLAG_LEFT
                if (horzPressed == FLAG_BOTH) {
                    updateStateOnVert(target)
                } else {
                    updateDirection(target, Direction.LEFT)
                }
            }

            Keyboard.Key.RIGHT -> {
                horzPressed = horzPressed or FLAG_RIGHT
                if (horzPressed == FLAG_BOTH) {
                    updateStateOnVert(target)
                } else {
                    updateDirection(target, Direction.RIGHT)
                }
            }

            Keyboard.Key.UP -> {
                vertPressed = vertPressed or FLAG_UP
                if (vertPressed == FLAG_BOTH) {
                    updateStateOnHorz(target)
                } else {
                    updateDirection(target, Direction.UP)
                }
            }

            Keyboard.Key.DOWN -> {
                vertPressed = vertPressed or FLAG_DOWN
                if (vertPressed == FLAG_BOTH) {
                    updateStateOnHorz(target)
                } else {
                    updateDirection(target, Direction.DOWN)
                }
            }

            Keyboard.Key.ACTION -> {
                target.startShooting()
            }

            else -> Unit
        }
    }

    private fun updateStateOnHorz(target: PlayerTankHandle) {
        if (horzPressed != 0 && horzPressed != FLAG_BOTH) {
            updateDirection(target, if (horzPressed == FLAG_LEFT) Direction.LEFT else Direction.RIGHT)
        } else {
            target.isIdle = true
            targetDirection = null
        }
    }

    private fun updateStateOnVert(target: PlayerTankHandle) {
        if (vertPressed != 0 && vertPressed != FLAG_BOTH) {
            updateDirection(target, if (vertPressed == FLAG_UP) Direction.UP else Direction.DOWN)
        } else {
            target.isIdle = true
            targetDirection = null
        }
    }

    private fun updateDirection(target: PlayerTankHandle, direction: Direction) {
        if (target.canMove) {
            setDirection(target, direction)
            target.isIdle = false
        } else {
            target.isIdle = true
            targetDirection = direction
        }
    }

    private fun setDirection(target: PlayerTankHandle, direction: Direction) {
        target.direction = direction
        targetDirection = if (target.direction == direction) {
            null
        } else {
            direction
        }
    }

    private fun keyReleased(target: PlayerTankHandle, key: Keyboard.Key) {
        if (key == Keyboard.Key.LEFT) {
            horzPressed = horzPressed and FLAG_LEFT.inv()
            if (horzPressed == 0) {
                updateStateOnVert(target)
            } else {
                updateStateOnHorz(target)
            }
        }
        if (key == Keyboard.Key.RIGHT) {
            horzPressed = horzPressed and FLAG_RIGHT.inv()
            if (horzPressed == 0) {
                updateStateOnVert(target)
            } else {
                updateStateOnHorz(target)
            }
        }
        if (key == Keyboard.Key.UP) {
            vertPressed = vertPressed and FLAG_UP.inv()
            if (vertPressed == 0) {
                updateStateOnHorz(target)
            } else {
                updateStateOnVert(target)
            }
        }
        if (key == Keyboard.Key.DOWN) {
            vertPressed = vertPressed and FLAG_DOWN.inv()
            if (vertPressed == 0) {
                updateStateOnHorz(target)
            } else {
                updateStateOnVert(target)
            }
        }
        if (key == Keyboard.Key.ACTION) {
            target.stopShooting()
        }
    }

    fun dispose() {
        tank = null

        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}