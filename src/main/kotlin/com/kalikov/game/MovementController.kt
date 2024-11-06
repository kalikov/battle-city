package com.kalikov.game

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class MovementController(
    private val game: Game,
    private val gameField: GameFieldHandle,
    private val pauseManager: PauseManager,
    private val mainContainer: SpriteContainer,
    private val overlayContainer: SpriteContainer,
) : EventSubscriber {
    companion object {
        const val UPDATE_INTERVAL = 4

        private val subscriptions = setOf(
            EnemyFactory.EnemyCreated::class,
            EnemyFactory.LastEnemyDestroyed::class,
            SpriteContainer.Added::class,
            Level.GameOver::class,
        )
    }

    private val timer = BasicTimer(game.clock, UPDATE_INTERVAL, ::move)

    private var gameOver = false
    private var isEnemyMovement = false

    init {
        game.eventManager.addSubscriber(this, subscriptions)
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
        mainContainer.forEach { sprite ->
            if (sprite is Bullet && !sprite.isDestroyed) {
                if (sprite.move()) {
                    detectCollisionsForBullet(sprite)
                }
            }
        }
    }

    private fun detectCollisionsForBullet(bullet: Bullet) {
        if (!gameField.bounds.contains(bullet.bounds)) {
            bullet.outOfBounds()
            return
        }
        var explode: Boolean? = null
        var tankHit = false
        if (gameField.walls.hit(bullet)) {
            explode = true
        }
        if (gameField.base.bounds.intersects(bullet.bounds)) {
            if (!gameField.base.isHit) {
                gameField.base.hit()
                explode = true
            }
        }
        mainContainer.forEach { sprite ->
            if (bullet !== sprite && !sprite.isDestroyed) {
                if (sprite is Tank && bullet.bounds.intersects(sprite.hitRect) && !tankHit) {
                    if (isBulletCollidable(bullet, sprite)) {
                        explode = if (sprite.canBeDestroyed) {
                            sprite.hit(bullet)
                            tankHit = true
                            true
                        } else {
                            explode == true
                        }
                    }
                } else if (sprite is Bullet && bulletIntersects(bullet, sprite)) {
                    sprite.hit(false)
                    explode = explode == true
                }
            }
        }
        explode?.let { bullet.hit(it) }
    }

    private fun isBulletCollidable(bullet: Bullet, tank: Tank): Boolean {
        val bulletTank = bullet.tank
        return bulletTank !== tank && (tank !is EnemyTank || bulletTank !is EnemyTank) && tank.isCollidable
    }

    private fun bulletIntersects(bullet1: Bullet, bullet2: Bullet): Boolean {
        return bullet1.bounds.intersects(bullet2.bounds) && isBulletCollidable(bullet1, bullet2)
    }

    private fun isBulletCollidable(bullet1: Bullet, bullet2: Bullet): Boolean {
        return !(bullet1.tank is EnemyTank && bullet2.tank is EnemyTank)
    }

    private fun moveTanks() {
        var isPlayerMovement = false
        mainContainer.forEach { sprite ->
            if (sprite is Tank && !sprite.isDestroyed) {
                move(sprite)
                if (sprite.canMove && !sprite.isIdle && sprite is PlayerTank) {
                    isPlayerMovement = true
                }
            }
        }
        if (isPlayerMovement && !gameOver) {
            if (!game.soundManager.stageStart.isPlaying && !game.soundManager.playerMovement.isPlaying) {
                game.soundManager.playerMovement.loop()
                game.soundManager.enemyMovement.stop()
            }
        } else {
            if (isEnemyMovement && !gameOver) {
                if (!game.soundManager.stageStart.isPlaying && !game.soundManager.enemyMovement.isPlaying) {
                    game.soundManager.enemyMovement.loop()
                }
            } else {
                game.soundManager.enemyMovement.stop()
            }
            game.soundManager.playerMovement.stop()
        }
    }

    private fun move(tank: Tank) {
        if (!tank.canMove) {
            return
        }
        when (tank) {
            is EnemyTank -> {
                if (!tank.isIdle && tank.move { !hasCollisionForTank(tank) }) {
                    detectBulletCollisionForTank(tank)
                }
            }

            is PlayerTank -> {
                val isSlippingMove = tank.isSlipping
                if ((!tank.isIdle || isSlippingMove) && tank.move { !hasCollisionForTank(tank) }) {
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

    private fun detectPowerUpCollisionForTank(tank: PlayerTank) {
        overlayContainer.iterateWhile { sprite ->
            if (sprite is PowerUp) {
                if (sprite.bounds.intersects(tank.hitRect)) {
                    sprite.pick(tank)
                }
                return@iterateWhile false
            }
            true
        }
    }

    private fun detectBulletCollisionForTank(tank: Tank) {
        mainContainer.iterateWhile { sprite ->
            if (sprite is Bullet && isBulletCollision(sprite, tank) && tank.hitRect.intersects(sprite.bounds)) {
                if (tank.canBeDestroyed) {
                    tank.hit(sprite)
                }
                sprite.hit(tank.canBeDestroyed)
                if (tank.isDestroyed) {
                    return@iterateWhile false
                }
            }
            true
        }
    }

    private fun hasCollisionForTank(tank: Tank): Boolean {
        when (tank.direction) {
            Direction.UP -> {
                if (tank.y - 1 < gameField.bounds.top) {
                    return true
                }
                return isCollisionForTank(tank, px(0), px(-1))
            }

            Direction.LEFT -> {
                if (tank.x - 1 < gameField.bounds.left) {
                    return true
                }
                return isCollisionForTank(tank, px(-1), px(0))
            }

            Direction.DOWN -> {
                if (tank.bottom + 1 > gameField.bounds.bottom) {
                    return true
                }
                return isCollisionForTank(tank, px(0), px(1))
            }

            Direction.RIGHT -> {
                if (tank.right + 1 > gameField.bounds.right) {
                    return true
                }
                return isCollisionForTank(tank, px(1), px(0))
            }
        }
    }

    private fun isTankOnIce(tank: Tank): Boolean {
        return gameField.ground.isTankOnIce(tank)
    }

    private fun isCollisionForTank(tank: Tank, dx: Pixel, dy: Pixel): Boolean {
        if (gameField.walls.collides(tank, dx, dy) ||
            gameField.ground.collides(tank, dx, dy) ||
            intersects(tank.bounds, gameField.base.bounds, dx, dy)
        ) {
            return true
        }
        return !mainContainer.iterateWhile { sprite ->
            if (tank !== sprite && !sprite.isDestroyed) {
                if (tank.direction == Direction.DOWN && sprite.bottom > tank.bottom ||
                    tank.direction == Direction.UP && sprite.top < tank.top ||
                    tank.direction == Direction.LEFT && sprite.left < tank.left ||
                    tank.direction == Direction.RIGHT && sprite.right > tank.right
                ) {
                    if (isTankCollision(sprite) && intersects(tank.bounds, sprite.hitRect, dx, dy)) {
                        return@iterateWhile false
                    }
                }
            }
            true
        }
    }

    private fun intersects(rect1: PixelRect, rect2: PixelRect, dx: Pixel, dy: Pixel): Boolean {
        return rect1.left + dx <= rect2.right && rect1.right + dx >= rect2.left &&
                rect1.top + dy <= rect2.bottom && rect1.bottom + dy >= rect2.top
    }

    @OptIn(ExperimentalContracts::class)
    private fun isTankCollision(target: Sprite): Boolean {
        contract {
            returns(true) implies (target is Tank)
        }
        return target is Tank && target.isCollidable
    }

    private fun isBulletCollision(bullet: Bullet, tank: Tank): Boolean {
        val bulletTank = bullet.tank
        return bulletTank !== tank && (tank !is EnemyTank || bulletTank !is EnemyTank) && tank.isCollidable && !bullet.isDestroyed
    }

    private fun detectCollisionsForPowerUp(powerUp: PowerUp) {
        mainContainer.iterateWhile { sprite ->
            if (powerUp !== sprite) {
                if (sprite is PlayerTank && powerUp.bounds.intersects(sprite.bounds)) {
                    powerUp.pick(sprite)
                    return@iterateWhile false
                }
            }
            true
        }
    }

    override fun notify(event: Event) {
        when (event) {
            is SpriteContainer.Added -> {
                when (event.sprite) {
                    is Bullet -> detectCollisionsForBullet(event.sprite)
                    is Tank -> {
                        if (event.sprite is PlayerTank) {
                            detectPowerUpCollisionForTank(event.sprite)
                        }
                        detectBulletCollisionForTank(event.sprite)
                    }

                    is PowerUp -> detectCollisionsForPowerUp(event.sprite)
                }
            }

            is Level.GameOver -> {
                gameOver = true
                stopMovementSound()
            }

            is EnemyFactory.EnemyCreated -> {
                isEnemyMovement = true
            }

            is EnemyFactory.LastEnemyDestroyed -> {
                isEnemyMovement = false
            }

            else -> Unit
        }
    }

    private fun stopMovementSound() {
        game.soundManager.playerMovement.stop()
        game.soundManager.enemyMovement.stop()
    }

    fun dispose() {
        timer.stop()

        stopMovementSound()

        game.eventManager.removeSubscriber(this, subscriptions)
    }
}