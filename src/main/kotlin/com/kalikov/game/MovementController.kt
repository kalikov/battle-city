package com.kalikov.game

import java.time.Clock
import kotlin.math.abs

class MovementController(
    private val eventManager: EventManager,
    private val pauseManager: PauseManager,
    private val bounds: Rect,
    private val spriteContainer: SpriteContainer,
    clock: Clock
) : EventSubscriber {
    companion object {
        const val UPDATE_INTERVAL = 4
    }

    private val timer = BasicTimer(clock, UPDATE_INTERVAL, ::move)

    init {
        eventManager.addSubscriber(this, setOf(SpriteContainer.Added::class))
    }

    fun update() {
        if (timer.isStopped) {
            timer.restart()
        }
        timer.update()
    }

    private fun move(count: Int) {
        if (!pauseManager.isPaused) {
            for (i in 0 until count) {
                moveBullets()
                moveTanks()
            }
        }
    }

    private fun moveBullets() {
        for (sprite in spriteContainer.sprites) {
            if (sprite is Bullet && !sprite.isDestroyed) {
                move(sprite)
            }
        }
    }

    private fun move(bullet: Bullet) {
        bullet.moveCountDown.update()
        if (bullet.moveCountDown.stopped) {
            bullet.moveCountDown.restart()
            when (bullet.direction) {
                Direction.RIGHT -> bullet.setPosition(bullet.x + 1, bullet.y)
                Direction.LEFT -> bullet.setPosition(bullet.x - 1, bullet.y)
                Direction.UP -> bullet.setPosition(bullet.x, bullet.y - 1)
                Direction.DOWN -> bullet.setPosition(bullet.x, bullet.y + 1)
            }
            detectCollisionsForBullet(bullet)
        }
    }

    private fun detectCollisionsForBullet(bullet: Bullet) {
        if (!bounds.contains(bullet.bounds)) {
            bullet.outOfBounds()
            return
        }
        val sprites = spriteContainer.sprites
        var explode: Boolean? = null
        var tankHit = false
        sprites.forEach { sprite ->
            if (bullet !== sprite && !sprite.isDestroyed) {
                if (sprite is Base && bullet.bounds.intersects(sprite.bounds)) {
                    if (!sprite.isHit) {
                        sprite.hit()
//                        eventManager.fireEvent(Base.Hit(sprite))
                        explode = true
                    }
                } else if (sprite is Wall && bullet.bounds.intersects(sprite.hitRect)) {
                    sprite.hit(bullet)
                    explode = true
                } else if (sprite is Tank && bullet.bounds.intersects(sprite.bounds) && !tankHit) {
                    if (bulletCollidable(bullet, sprite)) {
                        explode = if (sprite.canBeDestroyed) {
                            sprite.hit()
                            tankHit = true
                            true
                        } else {
                            explode == true
                        }
                    }
                } else if (sprite is Bullet && bulletIntersects(bullet, sprite)) {
                    explode = explode == true
                }
            }
        }
        explode?.let { bullet.hit(it) }
    }

    private fun bulletCollidable(bullet: Bullet, tank: Tank): Boolean {
        val bulletTank = bullet.tank
        return bulletTank !== tank && (!tank.isEnemy() || !bulletTank.isEnemy()) && tank.isCollidable
    }

    private fun bulletIntersects(bullet1: Bullet, bullet2: Bullet): Boolean {
        return bullet1.bounds.intersects(bullet2.bounds) && bulletCollidable(bullet1, bullet2)
    }

    private fun bulletCollidable(bullet1: Bullet, bullet2: Bullet): Boolean {
        return !(bullet1.tank.isEnemy() && bullet2.tank.isEnemy())
    }

    private fun moveTanks() {
        for (sprite in spriteContainer.sprites) {
            if (sprite is Tank && !sprite.isDestroyed) {
                move(sprite)
            }
        }
    }

    private fun move(tank: Tank) {
        if (!tank.canMove) {
            return
        }
        if (!tank.isIdle) {
            tank.moveCountDown.update()
            if (tank.moveCountDown.stopped) {
                tank.moveCountDown.restart()
                when (tank.direction) {
                    Direction.RIGHT -> tank.setPosition(tank.x + 1, tank.y)
                    Direction.LEFT -> tank.setPosition(tank.x - 1, tank.y)
                    Direction.UP -> tank.setPosition(tank.x, tank.y - 1)
                    Direction.DOWN -> tank.setPosition(tank.x, tank.y + 1)
                }
                detectCollisionsForTank(tank)
            }
        }
    }

    private fun detectCollisionsForTank(tank: Tank) {
        if (!bounds.contains(tank.bounds)) {
            tank.outOfBounds(bounds)
        }
        val sprites = spriteContainer.sprites
        sprites.forEach { sprite ->
            if (tank !== sprite && !sprite.isDestroyed) {
                if (tank.bounds.intersects(sprite.bounds)) {
                    if (wallCollision(sprite) || tankCollision(sprite) || baseCollision(sprite) || waterCollision(sprite)) {
                        resolveCollisionWithSprite(tank, sprite)
                    } else if (tank.isPlayer() && sprite is PowerUp) {
                        sprite.pick(tank)
                    } else if (sprite is Bullet && bulletCollision(sprite, tank)) {
                        if (tank.canBeDestroyed) {
                            tank.hit()
                        }
                        sprite.hit(tank.canBeDestroyed)
                    }
                }
            }
        }
    }

    private fun resolveCollisionWithSprite(tank: Tank, sprite: Sprite) {
        var moveX = 0
        var moveY = 0
        when (tank.direction) {
            Direction.RIGHT -> moveX = tank.right - sprite.left + 1
            Direction.LEFT -> moveX = tank.left - sprite.right - 1
            Direction.UP -> moveY = tank.top - sprite.bottom - 1
            Direction.DOWN -> moveY = tank.bottom - sprite.top + 1
        }
        if (abs(moveX) > tank.collisionResolvingMoveLimit || abs(moveY) > tank.collisionResolvingMoveLimit) {
            return
        }
        tank.setPosition(tank.x - moveX, tank.y - moveY)
    }

    private fun wallCollision(target: Sprite): Boolean {
        return target is Wall
    }

    private fun baseCollision(target: Sprite): Boolean {
        return target is Base
    }

    private fun waterCollision(target: Sprite): Boolean {
        return target is Water
    }

    private fun tankCollision(target: Sprite): Boolean {
        return target is Tank && target.isCollidable
    }

    private fun bulletCollision(bullet: Bullet, tank: Tank): Boolean {
        val bulletTank = bullet.tank
        return bulletTank !== tank && (!tank.isEnemy() || !bulletTank.isEnemy()) && tank.isCollidable
    }

    private fun detectCollisionsForPowerUp(powerUp: PowerUp) {
        val sprites = spriteContainer.sprites
        for (sprite in sprites) {
            if (powerUp !== sprite) {
                if (sprite is Tank && sprite.isPlayer() && powerUp.bounds.intersects(sprite.bounds)) {
                    powerUp.pick(sprite)
                    break
                }
            }
        }
    }

    override fun notify(event: Event) {
        when (event) {
            is SpriteContainer.Added -> {
                when (event.sprite) {
                    is Bullet -> detectCollisionsForBullet(event.sprite)
                    is Tank -> detectCollisionsForTank(event.sprite)
                    is PowerUp -> detectCollisionsForPowerUp(event.sprite)
                }
            }

            else -> Unit
        }
    }

    fun dispose() {
        timer.stop()

        eventManager.removeSubscriber(this, setOf(SpriteContainer.Added::class))
    }
}