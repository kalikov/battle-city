package com.kalikov.game

import kotlin.random.Random

class AITankController(
    eventManager: EventManager,
    private val tank: AITankHandle,
    private val base: PixelPoint,
    private val random: Random,
    params: AITankControllerParams = AITankControllerParams(),
) {
    private val shootTimer = PauseAwareTimer(eventManager, params.clock, params.shootInterval, ::shoot)
    private val shootProbability = params.shootProbability

    private var directionTimer = PauseAwareTimer(eventManager, params.clock, params.directionUpdateInterval, ::changeDirection)
    private val directionUpdateProbability = params.directionUpdateProbability
    private val directionRetreatProbability = params.directionRetreatProbability

    init {
        tank.isIdle = false
    }

    private fun updateShoot() {
        if (shootTimer.isStopped) {
            shootTimer.restart()
        }
        shootTimer.update()
    }

    private fun shoot() {
        if (random.nextDouble() < shootProbability) {
            tank.shoot()
        }
    }

    private fun updateDirection() {
        if (directionTimer.isStopped) {
            directionTimer.restart()
        }
        directionTimer.update()
    }

    private fun changeDirection() {
        if (random.nextDouble() < directionUpdateProbability) {
            val n = random.nextDouble()
            var direction = Direction.DOWN

            if (base.y > tank.y) {
                direction = Direction.DOWN
                if (n < directionRetreatProbability) {
                    direction = arrayOf(Direction.UP, Direction.LEFT, Direction.RIGHT).random(random)
                }
            } else if (base.y == tank.y) {
                if (base.x < tank.x) {
                    direction = Direction.LEFT
                    if (n < directionRetreatProbability) {
                        direction = arrayOf(Direction.UP, Direction.DOWN, Direction.RIGHT).random(random)
                    }
                } else if (base.x > tank.x) {
                    direction = Direction.RIGHT
                    if (n < directionRetreatProbability) {
                        direction = arrayOf(
                            Direction.UP,
                            Direction.LEFT,
                            Direction.DOWN
                        ).random(random)
                    }
                }
            } else {
                direction = Direction.UP
                if (n < directionRetreatProbability) {
                    direction = arrayOf(Direction.DOWN, Direction.LEFT, Direction.RIGHT).random(random)
                }
            }

            tank.direction = direction
        }
    }

    fun update() {
        updateShoot()
        updateDirection()
    }

    fun dispose() {
        shootTimer.dispose()
        directionTimer.dispose()
    }
}