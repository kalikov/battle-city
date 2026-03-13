package com.kalikov.game

import com.kalikov.engine.BasicTimer
import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.Pixel
import com.kalikov.engine.px
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class MovementController(
    private val game: BattleCityGame,
    private val gameField: GameFieldHandle,
    private val pauseManager: PauseManager,
    private val bulletsManager: BulletsManager,
    private val playerTanksManager: PlayerTanksManager,
    private val enemyTanksManager: EnemyTanksManager,
    private val powerUpManager: PowerUpManager,
) : EventSubscriber {
    companion object {
        const val UPDATE_INTERVAL = 4

        private val subscriptions = arrayOf(
            GameEnemyTanksManager.EnemyCreated::class,
            GameEnemyTanksManager.LastEnemyDestroyed::class,
            GamePlayerTanksManager.PlayerTankCreated::class,
            PowerUpManager.PowerUpCreated::class,
            Tank.Shoot::class,
            Level.GameOver::class,
        )
    }

    override val identity get() = Globals.IDENTITY_MOVEMENT_CONTROLLER

    private val timer = BasicTimer(game.clock, UPDATE_INTERVAL, ::move)

    private var gameOver = false
    private var isEnemyMovement = false

    fun activate() {
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
            repeat(count) {
                moveBullets()
                moveTanks()
            }
        }
    }

    private fun moveBullets() {
        bulletsManager.forEach { bullet ->
            if (!bullet.isDestroyed && bullet.move()) {
                detectCollisionsForBullet(bullet)
            }
        }
    }

    private fun detectCollisionsForBullet(bullet: Bullet) {
        if (!gameField.bounds.contains(bullet.bounds)) {
            bullet.outOfBounds()
            return
        }
        var explode: Boolean? = null
        bulletsManager.iterateWhile { sprite ->
            if (bullet !== sprite && !sprite.isDestroyed && bulletIntersects(bullet, sprite)) {
                sprite.hit(false)
                explode = false
            }
            explode == null
        }

        fun detectCollisionForTank(tank: Tank) {
            if (!tank.isDestroyed && tank.isCollidable && bullet.tank !== tank && bullet.bounds.intersects(tank.hitRect)) {
                explode = if (tank.canBeDestroyed) {
                    tank.hit(bullet)
                    true
                } else {
                    explode == true
                }
            }
        }

        if (explode == null && bullet.tank !is EnemyTank) {
            enemyTanksManager.iterateWhile { tank ->
                detectCollisionForTank(tank)
                explode == null
            }
        }
        if (explode == null) {
            playerTanksManager.iterateWhile { tank ->
                detectCollisionForTank(tank)
                explode == null
            }
        }
        if (explode == null && !gameField.base.isHit && gameField.base.bounds.intersects(bullet.bounds)) {
            gameField.base.hit()
            explode = true
        }
        explode = if (gameField.walls.hit(bullet)) true else explode
        explode?.let { bullet.hit(it) }
    }

    private fun bulletIntersects(bullet1: Bullet, bullet2: Bullet): Boolean {
        return bullet1.bounds.intersects(bullet2.bounds) && isBulletCollidable(bullet1, bullet2)
    }

    private fun isBulletCollidable(bullet1: Bullet, bullet2: Bullet): Boolean {
        return !(bullet1.tank is EnemyTank && bullet2.tank is EnemyTank)
    }

    private fun moveTanks() {
        var isPlayerMovement = false
        playerTanksManager.forEach { tank ->
            if (!tank.isDestroyed && tank.canMove) {
                isPlayerMovement = isPlayerMovement || !tank.isIdle
                move(tank)
            }
        }
        enemyTanksManager.forEach { tank ->
            if (!tank.isDestroyed && tank.canMove) {
                move(tank)
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

    private fun move(tank: PlayerTank) {
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

    private fun move(tank: EnemyTank) {
        if (!tank.isIdle && tank.move { !hasCollisionForTank(tank) }) {
            detectBulletCollisionForTank(tank)
        }
    }

    private fun detectPowerUpCollisionForTank(tank: PlayerTank) {
        powerUpManager.powerUp?.let { sprite ->
            if (sprite.bounds.intersects(tank.hitRect)) {
                sprite.pick(tank)
            }
        }
    }

    private fun detectBulletCollisionForTank(tank: Tank) {
        bulletsManager.iterateWhile { bullet ->
            if (isBulletCollision(bullet, tank) && tank.hitRect.intersects(bullet.bounds)) {
                if (tank.canBeDestroyed) {
                    tank.hit(bullet)
                }
                bullet.hit(tank.canBeDestroyed)
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
                return isCollisionForTank(tank, 0.px, -1.px)
            }

            Direction.LEFT -> {
                if (tank.x - 1 < gameField.bounds.left) {
                    return true
                }
                return isCollisionForTank(tank, -1.px, 0.px)
            }

            Direction.DOWN -> {
                if (tank.bottom + 1 > gameField.bounds.bottom) {
                    return true
                }
                return isCollisionForTank(tank, 0.px, 1.px)
            }

            Direction.RIGHT -> {
                if (tank.right + 1 > gameField.bounds.right) {
                    return true
                }
                return isCollisionForTank(tank, 1.px, 0.px)
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

        fun detectCollisionForTank(sprite: Tank): Boolean {
            if (tank !== sprite && !sprite.isDestroyed) {
                if (tank.direction == Direction.DOWN && sprite.bottom > tank.bottom ||
                    tank.direction == Direction.UP && sprite.top < tank.top ||
                    tank.direction == Direction.LEFT && sprite.left < tank.left ||
                    tank.direction == Direction.RIGHT && sprite.right > tank.right
                ) {
                    if (isTankCollision(sprite) && intersects(tank.bounds, sprite.hitRect, dx, dy)) {
                        return true
                    }
                }
            }
            return false
        }

        return !playerTanksManager.iterateWhile { sprite ->
            !detectCollisionForTank(sprite)
        } || !enemyTanksManager.iterateWhile { sprite ->
            !detectCollisionForTank(sprite)
        }
    }

    private fun intersects(rect1: PixelRect, rect2: PixelRect, dx: Pixel, dy: Pixel): Boolean {
        return rect1.left + dx <= rect2.right && rect1.right + dx >= rect2.left &&
                rect1.top + dy <= rect2.bottom && rect1.bottom + dy >= rect2.top
    }

    @OptIn(ExperimentalContracts::class)
    private fun isTankCollision(target: AbstractSprite): Boolean {
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
        playerTanksManager.iterateWhile { sprite ->
            if (!powerUp.bounds.intersects(sprite.bounds)) {
                true
            } else {
                powerUp.pick(sprite)
                false
            }
        }
    }

    override fun notify(event: Event) {
        when (event) {
            is GamePlayerTanksManager.PlayerTankCreated -> {
                detectPowerUpCollisionForTank(event.tank)
                detectBulletCollisionForTank(event.tank)
            }

            is Tank.Shoot -> {
                detectCollisionsForBullet(event.bullet)
            }

            is PowerUpManager.PowerUpCreated -> {
                detectCollisionsForPowerUp(event.powerUp)
            }

            is Level.GameOver -> {
                gameOver = true
                stopMovementSound()
            }

            is GameEnemyTanksManager.EnemyCreated -> {
                isEnemyMovement = true
                detectBulletCollisionForTank(event.enemy)
            }

            is GameEnemyTanksManager.LastEnemyDestroyed -> {
                isEnemyMovement = false
            }

            else -> Unit
        }
    }

    private fun stopMovementSound() {
        game.soundManager.playerMovement.stop()
        game.soundManager.enemyMovement.stop()
    }

    fun deactivate() {
        timer.stop()

        stopMovementSound()

        game.eventManager.removeSubscriber(this, subscriptions)
    }
}