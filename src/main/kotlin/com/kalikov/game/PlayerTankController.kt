package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventManager
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.Keyboard
import com.kalikov.engine.LeaksDetector

class PlayerTankController(
    private val eventManager: EventManager,
    private val pauseManager: PauseManager,
    private val playerTanksManager: PlayerTanksManager,
    val player: Player,
) : EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf(
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

    override val identity get() = Globals.IDENTITY_PLAYER_CONTROLLER + player.index

    private var isActive = true
    private var horzPressed: Int = 0

    private var vertPressed: Int = 0
    private var targetDirection: Direction? = null

    fun activate() {
        LeaksDetector.add(this)

        isActive = true
        horzPressed = 0
        vertPressed = 0
        targetDirection = null

        eventManager.addSubscriber(this, subscriptions)
    }

    fun deactivate() {
        playerTanksManager.getTank(player)?.let {
            it.isIdle = true
            it.stopShooting()
        }

        eventManager.removeSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        when (event) {
            is Keyboard.KeyPressed -> {
                if (event.playerIndex == player.index) {
                    keyPressed(event.key)
                }
            }

            is Keyboard.KeyReleased -> {
                if (event.playerIndex == player.index) {
                    keyReleased(event.key)
                }
            }

            is BaseExplosion.Destroyed -> {
                playerTanksManager.getTank(player)?.let {
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
        playerTanksManager.getTank(player)?.let {
            if (isActive) {
                targetDirection?.let { direction ->
                    if (it.canMove && it.direction != direction) {
                        setDirection(it, direction)
                    }
                }
                if (it.isIdle && it.canMove && (horzPressed != 0 || vertPressed != 0) && (horzPressed != FLAG_BOTH || vertPressed != FLAG_BOTH)) {
                    it.isIdle = false
                }
            }
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
                    updateDirection(Direction.LEFT)
                }
            }

            Keyboard.Key.RIGHT -> {
                horzPressed = horzPressed or FLAG_RIGHT
                if (horzPressed == FLAG_BOTH) {
                    updateStateOnVert()
                } else {
                    updateDirection(Direction.RIGHT)
                }
            }

            Keyboard.Key.UP -> {
                vertPressed = vertPressed or FLAG_UP
                if (vertPressed == FLAG_BOTH) {
                    updateStateOnHorz()
                } else {
                    updateDirection(Direction.UP)
                }
            }

            Keyboard.Key.DOWN -> {
                vertPressed = vertPressed or FLAG_DOWN
                if (vertPressed == FLAG_BOTH) {
                    updateStateOnHorz()
                } else {
                    updateDirection(Direction.DOWN)
                }
            }

            Keyboard.Key.ACTION -> {
                playerTanksManager.getTank(player)?.startShooting()
            }

            else -> Unit
        }
    }

    private fun updateStateOnHorz() {
        if (horzPressed != 0 && horzPressed != FLAG_BOTH) {
            updateDirection(if (horzPressed == FLAG_LEFT) Direction.LEFT else Direction.RIGHT)
        } else {
            playerTanksManager.getTank(player)?.isIdle = true
            targetDirection = null
        }
    }

    private fun updateStateOnVert() {
        if (vertPressed != 0 && vertPressed != FLAG_BOTH) {
            updateDirection(if (vertPressed == FLAG_UP) Direction.UP else Direction.DOWN)
        } else {
            playerTanksManager.getTank(player)?.isIdle = true
            targetDirection = null
        }
    }

    private fun updateDirection(direction: Direction) {
        val target = playerTanksManager.getTank(player)
        if (target != null && target.canMove) {
            setDirection(target, direction)
            target.isIdle = false
        } else {
            target?.isIdle = true
            targetDirection = direction
        }
    }

    private fun setDirection(target: PlayerTankHandle, direction: Direction) {
        target.direction = direction
        targetDirection = direction
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
            playerTanksManager.getTank(player)?.stopShooting()
        }
    }

    fun dispose() {
//        tank = null

        LeaksDetector.remove(this)
    }
}