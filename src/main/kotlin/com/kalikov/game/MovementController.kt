package com.kalikov.game

import java.time.Clock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.min

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
                if (sprite.move()) {
                    detectCollisionsForBullet(sprite)
                }
            }
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
                        explode = true
                    }
                } else if (sprite is Wall && bullet.bounds.intersects(sprite.hitRect)) {
                    sprite.hit(bullet)
                    explode = true
                } else if (sprite is Tank && bullet.bounds.intersects(sprite.bounds) && !tankHit) {
                    if (isBulletCollidable(bullet, sprite)) {
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

    private fun isBulletCollidable(bullet: Bullet, tank: Tank): Boolean {
        val bulletTank = bullet.tank
        return bulletTank !== tank && (!tank.isEnemy || !bulletTank.isEnemy) && tank.isCollidable
    }

    private fun bulletIntersects(bullet1: Bullet, bullet2: Bullet): Boolean {
        return bullet1.bounds.intersects(bullet2.bounds) && isBulletCollidable(bullet1, bullet2)
    }

    private fun isBulletCollidable(bullet1: Bullet, bullet2: Bullet): Boolean {
        return !(bullet1.tank.isEnemy && bullet2.tank.isEnemy)
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
        val isSlippingMove = tank.isSlipping
        if (!tank.isIdle || isSlippingMove) {
            if (tank.isEnemy) {
                if (tank.move { !hasCollisionForEnemyTank(tank) }) {
                    detectBulletCollisionForTank(tank)
                }
            } else {
                if (tank.move { !hasCollisionForPlayerTank(tank) }) {
                    detectPowerUpCollisionForTank(tank)
                    detectBulletCollisionForTank(tank)
                    if (isTankOnIce(tank)) {
                        if (!isSlippingMove) {
                            tank.startSlipping()
                        }
                    } else {
                        tank.stopSlipping()
                    }
                }
            }
        }
    }

    private fun detectPowerUpCollisionForTank(tank: Tank) {
        for (sprite in spriteContainer.sprites) {
            if (sprite is PowerUp) {
                if (tank.bounds.intersects(sprite.bounds)) {
                    sprite.pick(tank)
                }
                break
            }
        }
    }

    private fun detectBulletCollisionForTank(tank: Tank) {
        for (sprite in spriteContainer.sprites) {
            if (sprite is Bullet && isBulletCollision(sprite, tank) && tank.bounds.intersects(sprite.bounds)) {
                if (tank.canBeDestroyed) {
                    tank.hit()
                }
                sprite.hit(tank.canBeDestroyed)
                if (tank.isDestroyed) {
                    break
                }
            }
        }
    }

    private fun hasCollisionForEnemyTank(tank: Tank): Boolean {
        return hasCollisionForTank(tank, this::isCollisionForEnemyTank)
    }

    private fun hasCollisionForPlayerTank(tank: Tank): Boolean {
        return hasCollisionForTank(tank, this::isCollisionForPlayerTank)
    }

    private inline fun hasCollisionForTank(
        tank: Tank,
        isCollisionFunction: (Tank, Sprite, Int, Int) -> Boolean
    ): Boolean {
        val sprites = spriteContainer.sprites
        when (tank.direction) {
            Direction.UP -> {
                if (tank.y - 1 < bounds.top) {
                    return true
                }
                sprites.forEach { sprite ->
                    if (tank !== sprite && !sprite.isDestroyed && sprite.y < tank.y) {
                        if (isCollisionFunction(tank, sprite, 0, -1)) {
                            return true
                        }
                    }
                }
            }

            Direction.LEFT -> {
                if (tank.x - 1 < bounds.left) {
                    return true
                }
                sprites.forEach { sprite ->
                    if (tank !== sprite && !sprite.isDestroyed && sprite.x < tank.x) {
                        if (isCollisionFunction(tank, sprite, -1, 0)) {
                            return true
                        }
                    }
                }
            }

            Direction.DOWN -> {
                if (tank.bottom + 1 > bounds.bottom) {
                    return true
                }
                sprites.forEach { sprite ->
                    if (tank !== sprite && !sprite.isDestroyed && sprite.bottom > tank.bottom) {
                        if (isCollisionFunction(tank, sprite, 0, 1)) {
                            return true
                        }
                    }
                }
            }

            Direction.RIGHT -> {
                if (tank.right + 1 > bounds.right) {
                    return true
                }
                sprites.forEach { sprite ->
                    if (tank !== sprite && !sprite.isDestroyed && sprite.right > tank.right) {
                        if (isCollisionFunction(tank, sprite, 1, 0)) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun isTankOnIce(tank: Tank): Boolean {
        val area = tank.bounds.area
        var firmArea = area
        spriteContainer.sprites.forEach { sprite ->
            if (tank !== sprite && !sprite.isDestroyed && sprite is Ice) {
                firmArea -= sprite.bounds.intersection(tank.bounds)?.area ?: 0
            }
        }
        return firmArea <= area / 2
    }

    private fun isCollisionForEnemyTank(enemy: Tank, sprite: Sprite, dx: Int, dy: Int): Boolean {
        val isStaticCollision = isWallCollision(sprite) || isBaseCollision(sprite) || isWaterCollision(sprite)
        return isStaticCollision && intersects(enemy, sprite, dx, dy)
                || isTankCollision(sprite) && isEnemyIntersectingTank(enemy, sprite, dx, dy)
    }

    private fun isCollisionForPlayerTank(enemy: Tank, sprite: Sprite, dx: Int, dy: Int): Boolean {
        return (isWallCollision(sprite) || isBaseCollision(sprite) || isWaterCollision(sprite) || isTankCollision(sprite))
                && intersects(enemy, sprite, dx, dy)
    }

    private fun isEnemyIntersectingTank(enemy: Tank, tank: Tank, dx: Int, dy: Int): Boolean {
        return !tank.isPlayer && intersects(enemy, tank, dx, dy)
                || tank.isPlayer && intersectsPlayer(enemy, tank, dx, dy)
    }

    private fun intersects(tank: Tank, sprite: Sprite, dx: Int, dy: Int): Boolean {
        return tank.left + dx <= sprite.right && tank.right + dx >= sprite.left &&
                tank.top + dy <= sprite.bottom && tank.bottom + dy >= sprite.top
    }

    private fun intersectsPlayer(tank: Tank, player: Tank, dx: Int, dy: Int): Boolean {
        val shrink = min((Globals.UNIT_SIZE - Bullet.SIZE) / 2, Tank.SIZE)
        return tank.left + dx <= player.right - shrink && tank.right + dx >= player.left + shrink &&
                tank.top + dy <= player.bottom - shrink && tank.bottom + dy >= player.top + shrink
    }

    private fun isWallCollision(target: Sprite) = target is Wall

    private fun isBaseCollision(target: Sprite) = target is Base

    private fun isWaterCollision(target: Sprite) = target is Water


    @OptIn(ExperimentalContracts::class)
    private fun isTankCollision(target: Sprite): Boolean {
        contract {
            returns(true) implies (target is Tank)
        }
        return target is Tank && target.isCollidable
    }

    private fun isBulletCollision(bullet: Bullet, tank: Tank): Boolean {
        val bulletTank = bullet.tank
        return bulletTank !== tank && (!tank.isEnemy || !bulletTank.isEnemy) && tank.isCollidable && !bullet.isDestroyed
    }

    private fun detectCollisionsForPowerUp(powerUp: PowerUp) {
        val sprites = spriteContainer.sprites
        for (sprite in sprites) {
            if (powerUp !== sprite) {
                if (sprite is Tank && sprite.isPlayer && powerUp.bounds.intersects(sprite.bounds)) {
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
                    is Tank -> {
                        if (event.sprite.isPlayer) {
                            detectPowerUpCollisionForTank(event.sprite)
                        }
                        detectBulletCollisionForTank(event.sprite)
                    }
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