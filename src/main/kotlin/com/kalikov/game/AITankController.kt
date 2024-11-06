package com.kalikov.game

import kotlin.math.abs
import kotlin.random.Random

class AITankController(
    eventManager: EventManager,
    private val tank: AITankHandle,
    private val base: PixelPoint,
    private val players: Set<PlayerTankHandle>,
    private val random: Random,
    params: AITankControllerParams = AITankControllerParams(),
) {
    private companion object {
        private const val STUCK_LIMIT = 4
    }

    private val shootTimer = PauseAwareTimer(eventManager, params.clock, params.shootInterval, ::shoot)
    private val shootProbability = params.shootProbability

    private var directionTimer = PauseAwareTimer(eventManager, params.clock, params.directionUpdateInterval, ::changeDirection)
    private val directionUpdateProbability = params.directionUpdateProbability
    private val directionRetreatProbability = params.directionRetreatProbability

    private var stuckCounter = 0
    private var prevX = tank.x
    private var prevY = tank.y

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
        if (stuckCounter / tank.moveFrequency > STUCK_LIMIT) {
            adjustDirection()
        } else if (random.nextDouble() < directionUpdateProbability) {
            val direction = calculateDirectionToClosestTarget()

            tank.direction = direction
        }
    }

    private fun adjustDirection() {
        tank.direction = when (tank.direction) {
            Direction.UP -> Direction.RIGHT
            Direction.RIGHT -> Direction.DOWN
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.UP
        }
        stuckCounter = 0
    }

    private fun calculateDirectionToClosestTarget(): Direction {
        val n = random.nextDouble()

        val closestPlayer = players.minByOrNull { distanceToMe(it.x, it.y) }
        val targetTop: Pixel
        val targetLeft: Pixel
        val targetRight: Pixel
        val targetBottom: Pixel
        if (closestPlayer == null || distanceToMe(base.x, base.y) < distanceToMe(closestPlayer.x, closestPlayer.y)) {
            targetLeft = base.x
            targetTop = base.y
            targetRight = targetLeft + Base.SIZE
            targetBottom = targetTop + Base.SIZE
        } else {
            targetTop = closestPlayer.hitRect.top
            targetLeft = closestPlayer.hitRect.left
            targetRight = closestPlayer.hitRect.right
            targetBottom = closestPlayer.hitRect.bottom
        }

        var direction: Direction
        if (targetTop > tank.hitRect.bottom) {
            direction = Direction.DOWN
            if (n < directionRetreatProbability) {
                direction = randomOf(Direction.UP, Direction.LEFT, Direction.RIGHT)
            }
        } else if (targetBottom < tank.hitRect.top) {
            direction = Direction.UP
            if (n < directionRetreatProbability) {
                direction = randomOf(Direction.DOWN, Direction.LEFT, Direction.RIGHT)
            }
        } else if (targetRight < tank.hitRect.left) {
            direction = Direction.LEFT
            if (n < directionRetreatProbability) {
                direction = randomOf(Direction.UP, Direction.DOWN, Direction.RIGHT)
            }
        } else if (targetLeft > tank.hitRect.right) {
            direction = Direction.RIGHT
            if (n < directionRetreatProbability) {
                direction = randomOf(Direction.UP, Direction.LEFT, Direction.DOWN)
            }
        } else {
            direction = if (tank.hitRect.left - targetLeft >= targetRight - tank.hitRect.right) Direction.LEFT else Direction.RIGHT
        }
        return direction
    }

    private fun distanceToMe(x: Pixel, y: Pixel): Int {
        return abs(tank.x.toInt() - x.toInt()) + abs(tank.y.toInt() - y.toInt())
    }

    private fun <T : Any> randomOf(vararg items: T): T {
        return items.random(random)
    }

    fun update() {
        updateShoot()
        updateDirection()
        updateStuckCounter()
        prevX = tank.x
        prevY = tank.y
    }

    private fun updateStuckCounter() {
        if (!tank.isIdle) {
            if (tank.x == prevX && tank.y == prevY) {
                stuckCounter++
            } else {
                stuckCounter = 0
            }
        } else {
            stuckCounter = 0
        }
    }

    fun dispose() {
        shootTimer.dispose()
        directionTimer.dispose()
    }
}