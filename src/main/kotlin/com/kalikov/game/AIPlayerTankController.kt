package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.Keyboard
import com.kalikov.engine.LeaksDetector
import com.kalikov.engine.Pixel
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.max
import com.kalikov.engine.min
import com.kalikov.engine.px
import kotlin.math.abs

class AIPlayerTankController(
    private val game: BattleCityGame,
    private val player: Player,
    private val gameField: GameField,
    private val playerTanksManager: PlayerTanksManager,
    private val enemyTanksManager: EnemyTanksManager,
    params: AIPlayerTankControllerParams = AIPlayerTankControllerParams(),
) : EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf(
            PowerUpManager.PowerUpCreated::class,
            PowerUpManager.PowerUpDestroyed::class,
            BaseExplosion.Destroyed::class,
            Keyboard.KeyPressed::class,
            Keyboard.KeyReleased::class,
        )

        private const val STUCK_LIMIT = 4

        private val CRITICAL_DISTANCE = Globals.TILE_SIZE.toInt() * 8
    }

    override val identity get() = Globals.IDENTITY_AI_PLAYER_CONTROLLER + player.index

    private val strategyTimer = PauseAwareTimer(NoopPauseManager, game.clock, params.strategyUpdateInterval, ::updateStrategy)

    private var prevX = 0.px
    private var prevY = 0.px

    private var isActive = true

    private lateinit var baseRect: TileRect

    private var stuckCounter = 0

    private var lineOfSight = LineOfSight()

    private var powerUp: PowerUp? = null

    private var target: AbstractSprite? = null

    init {
        LeaksDetector.add(this)
    }

    fun activate() {
        game.eventManager.addSubscriber(this, subscriptions)

        strategyTimer.stop()

        baseRect = TileRect(
            gameField.base.x.toTile() - 1,
            gameField.base.y.toTile() - 1,
            gameField.base.width.toTile() + 2,
            gameField.base.height.toTile() + 2,
        )
    }

    fun deactivate() {
        strategyTimer.stop()

        game.eventManager.removeSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        when (event) {
            is BaseExplosion.Destroyed -> {
                val tank = playerTanksManager.getTank(player)
                tank?.let {
                    it.stopShooting()
                    it.isIdle = true
                }
                isActive = false
            }

            is PowerUpManager.PowerUpCreated -> {
                powerUp = event.powerUp
                updateTarget()
            }

            is PowerUpManager.PowerUpDestroyed -> {
                if (event.powerUp === powerUp) {
                    powerUp = null
                    if (target === event.powerUp) {
                        target = null
                    }
                    updateTarget()
                }
            }

            else -> Unit
        }
    }

    fun update() {
        if (!isActive) {
            return
        }
        val tank = playerTanksManager.getTank(player)
        if (tank == null) {
            stuckCounter = 0
            target = null
            strategyTimer.stop()
            return
        }
        if (strategyTimer.isStopped) {
            strategyTimer.restart()
        }
        strategyTimer.update()

        if (target == null || tank.isIdle) {
            prevX = tank.x
            prevY = tank.y
            updateTarget()
        }

        updateLineOfSight()
        if (!tank.isIdle && !isInLineOfSight(baseRect)) {
            tank.startShooting()
        } else {
            tank.stopShooting()
        }
        updateStuckCounter(tank)
        prevX = tank.x
        prevY = tank.y
    }

    private fun updateLineOfSight() {
        val tank = playerTanksManager.getTank(player)
        tank?.let {
            val hitTop = (it.hitRect.top - gameField.bounds.y).toTile()
            val hitLeft = (it.hitRect.left - gameField.bounds.x).toTile()
            val hitRight = (it.hitRect.right - gameField.bounds.x).toTile()
            val hitBottom = (it.hitRect.bottom - gameField.bounds.y).toTile()
            when (it.direction) {
                Direction.UP -> calculateUpFront(hitTop, hitLeft, hitRight)
                Direction.LEFT -> calculateLeftFront(hitTop, hitLeft, hitBottom)
                Direction.DOWN -> calculateDownFront(hitLeft, hitRight, hitBottom)
                Direction.RIGHT -> calculateRightFront(hitTop, hitRight, hitBottom)
            }
        }
    }

    private fun calculateUpFront(hitTop: Tile, hitLeft: Tile, hitRight: Tile) {
        var i = 0
        for (x in hitLeft.toInt() .. hitRight.toInt()) {
            var height = 0.tiles
            for (y in hitTop.toInt() - 1 downTo 0) {
                if (gameField.walls.occupied(x.tiles, y.tiles)) {
                    height++
                    break
                }
                height++
            }
            lineOfSight.distances[i] = height
            i++
        }
    }

    private fun calculateDownFront(hitLeft: Tile, hitRight: Tile, hitBottom: Tile) {
        var i = 0
        for (x in hitLeft.toInt() .. hitRight.toInt()) {
            var height = 0.tiles
            for (y in hitBottom.toInt() + 1 until GameField.SIZE_IN_TILES.toInt()) {
                if (gameField.walls.occupied(x.tiles, y.tiles)) {
                    height++
                    break
                }
                height++
            }
            lineOfSight.distances[i] = height
            i++
        }
    }

    private fun calculateLeftFront(hitTop: Tile, hitLeft: Tile, hitBottom: Tile) {
        var i = 0
        for (y in hitTop.toInt() .. hitBottom.toInt()) {
            var width = 0.tiles
            for (x in hitLeft.toInt() - 1 downTo 0) {
                if (gameField.walls.occupied(x.tiles, y.tiles)) {
                    width++
                    break
                }
                width++
            }
            lineOfSight.distances[i] = width
            i++
        }
    }

    private fun calculateRightFront(hitTop: Tile, hitRight: Tile, hitBottom: Tile) {
        var i = 0
        for (y in hitTop.toInt() .. hitBottom.toInt()) {
            var width = 0.tiles
            for (x in hitRight.toInt() + 1 until GameField.SIZE_IN_TILES.toInt()) {
                if (gameField.walls.occupied(x.tiles, y.tiles)) {
                    width++
                    break
                }
                width++
            }
            lineOfSight.distances[i] = width
            i++
        }
    }

    private fun updateTarget() {
        var chosenEnemy: EnemyTank? = null
        var chosenDistanceToBase = 0
        var chosenDistanceToMe = 0
        enemyTanksManager.forEach { enemy ->
            if (!isBehindPartner(enemy)) {
                val enemyDistanceToBase = distanceToBase(enemy)
                val enemyDistanceToMe = distanceToMe(enemy)
                if (chosenEnemy == null
                    || enemyDistanceToBase < CRITICAL_DISTANCE && enemyDistanceToBase < chosenDistanceToBase
                    || enemyDistanceToMe < chosenDistanceToMe && CRITICAL_DISTANCE < chosenDistanceToBase) {
                    chosenEnemy = enemy
                    chosenDistanceToBase = enemyDistanceToBase
                    chosenDistanceToMe = enemyDistanceToMe
                }
            }
        }
        if (chosenEnemy != null) {
            target = chosenEnemy
            return
        }

        if (powerUp != null) {
            target = powerUp
            return
        }
    }

    private fun isBehindPartner(enemy: EnemyTank): Boolean {
        val tank = playerTanksManager.getTank(player)
        if (tank != null) {
            return !playerTanksManager.iterateWhile { partner ->
                val top = min(tank.hitRect.top, enemy.top)
                val left = min(tank.hitRect.left, enemy.left)
                val right = max(tank.hitRect.right, enemy.right)
                val bottom = max(tank.hitRect.bottom, enemy.bottom)

                val partnerRect = partner.hitRect
                if (left <= partnerRect.right && right >= partnerRect.left && top <= partnerRect.bottom && bottom >= partnerRect.top) {
                    !isInLineOfSight(partnerRect)
                } else {
                    true
                }
            }
        }
        return false
    }

    private fun isNearBase(enemy: EnemyTank): Boolean {
        return distance(gameField.base.center.x, gameField.base.center.y, enemy.center, enemy.middle) < CRITICAL_DISTANCE
    }

    private fun distanceToBase(enemy: EnemyTank): Int {
        return distance(gameField.base.center.x, gameField.base.center.y, enemy.center, enemy.middle)
    }

    private fun distanceToMe(enemy: EnemyTank): Int {
        val tank = playerTanksManager.getTank(player)
        return tank?.let {
            distance(it.x, it.y, enemy.x, enemy.y)
        } ?: Int.MAX_VALUE
    }

    private fun distance(x1: Pixel, y1: Pixel, x2: Pixel, y2: Pixel): Int {
        return abs(x2.toInt() - x1.toInt()) + abs(y2.toInt() - y1.toInt())
    }

    fun draw(surface: ScreenSurface) {
        if (game.config.debug) {
            val tank = playerTanksManager.getTank(player)
            tank?.let {
                lineOfSight.distances.forEachIndexed { index, distance ->
                    if (tank.direction == Direction.UP) {
                        surface.drawRect(
                            tank.hitRect.x + index.tiles.toPixel(),
                            tank.hitRect.y - distance.toPixel(),
                            1.tiles.toPixel(),
                            distance.toPixel(),
                            ARGB(0x66FF0000)
                        )
                    }
                }
                if (stuckCounter > 0) {
                    val stuckLength = stuckCounter / it.moveFrequency + if ((stuckCounter % it.moveFrequency) == 0) 0 else 1
                    surface.drawLine(
                        it.x,
                        it.y,
                        it.x - stuckLength,
                        it.y - stuckLength,
                        ARGB.RED
                    )
                    surface.drawLine(
                        it.x,
                        it.y + Tank.SIZE,
                        it.x - stuckLength,
                        it.y + Tank.SIZE + stuckLength,
                        ARGB.RED
                    )
                    surface.drawLine(
                        it.x + Tank.SIZE,
                        it.y,
                        it.x + Tank.SIZE + stuckLength,
                        it.y - stuckLength,
                        ARGB.RED
                    )
                    surface.drawLine(
                        it.x + Tank.SIZE,
                        it.y + Tank.SIZE,
                        it.x + Tank.SIZE + stuckLength,
                        it.y + Tank.SIZE + stuckLength,
                        ARGB.RED
                    )
                }
            }
        }
    }

    private fun updateStrategy() {
        val tank = playerTanksManager.getTank(player)
        tank?.let {
            updateTarget()

            target?.let { t ->
                if (stuckCounter / it.moveFrequency > STUCK_LIMIT) {
                    adjustDirection(it)
                } else {
                    moveTowardsTarget(it, t)
                }
            } ?: run {
                it.isIdle = true
            }
        }
    }

    private fun updateStuckCounter(tankHandle: PlayerTankHandle) {
        if (!tankHandle.isIdle) {
            if (tankHandle.x == prevX && tankHandle.y == prevY) {
                stuckCounter++
            } else {
                stuckCounter = 0
            }
        } else {
            stuckCounter = 0
        }
    }

    private fun adjustDirection(tankHandle: PlayerTankHandle) {
        tankHandle.direction = when (tankHandle.direction) {
            Direction.UP -> Direction.RIGHT
            Direction.RIGHT -> Direction.DOWN
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.UP
        }
        stuckCounter = 0
    }

    private fun moveTowardsTarget(tankHandle: PlayerTankHandle, t: AbstractSprite) {
        val tankTop = tankHandle.hitRect.top.toTile()
        val tankLeft = tankHandle.hitRect.left.toTile()
        val tankRight = tankHandle.hitRect.right.toTile()
        val tankBottom = tankHandle.hitRect.bottom.toTile()
        val targetTop = t.y.toTile()
        val targetLeft = t.x.toTile()
        val targetRight = t.right.toTile()
        val targetBottom = t.bottom.toTile()
        val direction = when {
            targetBottom < tankTop -> Direction.UP
            targetTop > tankBottom -> Direction.DOWN
            targetRight < tankLeft -> Direction.LEFT
            targetLeft > tankRight -> Direction.RIGHT
            else -> if (abs(targetTop.toInt() - tankBottom.toInt()) < abs(targetLeft.toInt() - tankRight.toInt())) {
                Direction.UP
            } else {
                Direction.LEFT
            }
        }
        tankHandle.direction = direction
        tankHandle.isIdle = false
    }

    private fun isInLineOfSight(rect: TileRect): Boolean {
        return lineOfSight.intersects(rect)
    }

    private fun isInLineOfSight(rect: PixelRect): Boolean {
        val top = (rect.y - gameField.bounds.y).toTile()
        val left = (rect.x - gameField.bounds.x).toTile()
        val right = left + rect.width.toTile()
        val bottom = top + rect.height.toTile()
        return lineOfSight.intersects(left, right, top, bottom)
    }

    fun dispose() {
        strategyTimer.stop()

        LeaksDetector.remove(this)
    }

    private class LineOfSight(
        val distances: Array<Tile> = Array(Tank.SIZE.toTile().toInt()) { 0.tiles }
    ) {
        fun intersects(rect: TileRect): Boolean {
            return false
        }

        fun intersects(rect: Tile, right: Tile, top: Tile, bottom: Tile): Boolean {
            return false
        }

    }
}