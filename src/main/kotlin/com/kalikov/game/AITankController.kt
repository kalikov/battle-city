package com.kalikov.game

import kotlin.math.abs
import kotlin.random.Random

class AITankController(
    private val eventManager: EventManager,
    private val tank: AITankHandle,
    private val base: PixelPoint,
    private val random: Random,
    params: AITankControllerParams = AITankControllerParams(),
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            PlayerTankFactory.PlayerTankCreated::class,
            PlayerTank.PlayerDestroyed::class,
        )
    }

    private val shootTimer = PauseAwareTimer(eventManager, params.clock, params.shootInterval, ::shoot)
    private val shootProbability = params.shootProbability

    private var directionTimer = PauseAwareTimer(eventManager, params.clock, params.directionUpdateInterval, ::changeDirection)
    private val directionUpdateProbability = params.directionUpdateProbability
    private val directionRetreatProbability = params.directionRetreatProbability

    private val players = mutableSetOf<PlayerTankHandle>()

    init {
        eventManager.addSubscriber(this, subscriptions)
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
            val direction = calculateDirectionToClosestTarget()

            tank.direction = direction
        }
    }

    private fun calculateDirectionToClosestTarget(): Direction {
        val n = random.nextDouble()
        var direction = Direction.DOWN

        val closestPlayer = players.minByOrNull { distanceToMe(it.x, it.y) }
        val targetX: Pixel
        val targetY: Pixel
        if (closestPlayer == null || distanceToMe(base.x, base.y) < distanceToMe(closestPlayer.x, closestPlayer.y)) {
            targetX = base.x
            targetY = base.y
        } else {
            targetX = closestPlayer.x
            targetY = closestPlayer.y
        }

        if (targetY > tank.y) {
            direction = Direction.DOWN
            if (n < directionRetreatProbability) {
                direction = randomOf(Direction.UP, Direction.LEFT, Direction.RIGHT)
            }
        } else if (targetY == tank.y) {
            if (targetX < tank.x) {
                direction = Direction.LEFT
                if (n < directionRetreatProbability) {
                    direction = randomOf(Direction.UP, Direction.DOWN, Direction.RIGHT)
                }
            } else if (targetX > tank.x) {
                direction = Direction.RIGHT
                if (n < directionRetreatProbability) {
                    direction = randomOf(Direction.UP, Direction.LEFT, Direction.DOWN)
                }
            }
        } else {
            direction = Direction.UP
            if (n < directionRetreatProbability) {
                direction = randomOf(Direction.DOWN, Direction.LEFT, Direction.RIGHT)
            }
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
    }

    override fun notify(event: Event) {
        when (event) {
            is PlayerTankFactory.PlayerTankCreated -> players.add(event.tank)
            is PlayerTank.PlayerDestroyed -> players.remove(event.tank)
            else -> Unit
        }
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)

        shootTimer.dispose()
        directionTimer.dispose()
    }
}