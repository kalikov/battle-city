package com.kalikov.game

import kotlin.random.Random

class AIPlayerTankController(
    private val game: Game,
    private val player: Player,
    private val gameField: GameField,
    private val random: Random = Random.Default,
    params: AIPlayerTankControllerParams = AIPlayerTankControllerParams(),
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            PlayerTankFactory.PlayerTankCreated::class,
            PlayerTank.PlayerDestroyed::class,
            PlayerTank.PlayerMoved::class,
            EnemyFactory.EnemyCreated::class,
            PowerUpFactory.PowerUpCreated::class,
            Sprite.Destroyed::class,
            Tank.Destroyed::class,
            Walls.Hit::class,
            BaseExplosion.Destroyed::class,
            Keyboard.KeyPressed::class,
            Keyboard.KeyReleased::class,
        )

        private const val STUCK_LIMIT = 4
    }

    private val strategyTimer =
        PauseAwareTimer(game.eventManager, game.clock, params.strategyUpdateInterval, ::updateStrategy)

    private var tank: PlayerTankHandle? = null
    private var prevX = px(0)
    private var prevY = px(0)

    private var isActive = true

    private val baseRect: TileRect

    private var stuckCounter = 0

    private var lineOfSight = emptyList<TileRect>()

    private val enemies = mutableSetOf<EnemyTank>()
    private var powerUp: PowerUp? = null

    private var target: Sprite? = null

    init {
        LeaksDetector.add(this)

        game.eventManager.addSubscriber(this, subscriptions)

        baseRect = TileRect(
            gameField.base.x.toTile() - 1,
            gameField.base.y.toTile() - 1,
            gameField.base.width.toTile() + 2,
            gameField.base.height.toTile() + 2,
        )
    }

    override fun notify(event: Event) {
        when (event) {
            is PlayerTankFactory.PlayerTankCreated -> {
                if (event.tank.player === player) {
                    tank = event.tank
                    prevX = event.tank.x
                    prevY = event.tank.y
                    updateLineOfSight()
                }
            }

            is PlayerTank.PlayerDestroyed -> {
                if (event.tank === tank) {
                    tank = null
                }
            }

            is PlayerTank.PlayerMoved -> {
                if (event.tank === tank) {
                    updateLineOfSight()
                }
            }

            is Walls.Hit -> {
                updateLineOfSight()
            }

            is EnemyFactory.EnemyCreated -> {
                enemies.add(event.enemy)
                updateTarget()
            }

            is Tank.Destroyed -> {
                if (enemies.remove(event.tank)) {
                    if (target === event.tank) {
                        target = null
                    }
                    updateTarget()
                }
            }

            is BaseExplosion.Destroyed -> {
                tank?.let {
                    it.stopShooting()
                    it.isIdle = true
                }
                isActive = false
            }

            is PowerUpFactory.PowerUpCreated -> {
                powerUp = event.powerUp
                updateTarget()
            }

            is Sprite.Destroyed -> {
                if (event.sprite is PowerUp && event.sprite === powerUp) {
                    powerUp = null
                    if (target === event.sprite) {
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
        if (strategyTimer.isStopped) {
            strategyTimer.restart()
        }
        strategyTimer.update()
        tank?.let {
            if (!it.isIdle && !isBaseInLineOfSight()) {
                it.startShooting()
            } else {
                it.stopShooting()
            }
            updateStuckCounter(it)
            prevX = it.x
            prevY = it.y
        }
    }

    private fun updateLineOfSight() {
        tank?.let {
            val hitTop = (it.hitRect.y - gameField.bounds.y).toTile()
            val hitLeft = (it.hitRect.x - gameField.bounds.x).toTile()
            val hitRight = (it.hitRect.right - gameField.bounds.x).toTile()
            val hitBottom = (it.hitRect.bottom - gameField.bounds.y).toTile()
            lineOfSight = when (it.direction) {
                Direction.UP -> calculateUpFront(hitTop, hitLeft, hitRight)
                Direction.LEFT -> calculateLeftFront(hitTop, hitLeft, hitBottom)
                Direction.DOWN -> calculateDownFront(hitLeft, hitRight, hitBottom)
                Direction.RIGHT -> calculateRightFront(hitTop, hitRight, hitBottom)
            }
        }
    }

    private fun calculateUpFront(hitTop: Tile, hitLeft: Tile, hitRight: Tile): List<TileRect> {
        val rects = mutableListOf<TileRect>()
        for (x in hitLeft.toInt() .. hitRight.toInt()) {
            var height = t(0)
            for (y in hitTop.toInt() - 1 downTo 0) {
                if (gameField.walls.occupied(t(x), t(y))) {
                    height++
                    break
                }
                height++
            }
            if (rects.isEmpty() || rects.last().height != height) {
                rects.add(TileRect(t(x), hitTop - height, t(1), height))
            } else {
                val last = rects.removeLast()
                rects.add(TileRect(last.x, hitTop - height, last.width + 1, height))
            }
        }
        return rects
    }

    private fun calculateDownFront(hitLeft: Tile, hitRight: Tile, hitBottom: Tile): List<TileRect> {
        val rects = mutableListOf<TileRect>()
        for (x in hitLeft.toInt() .. hitRight.toInt()) {
            var height = t(0)
            for (y in hitBottom.toInt() + 1 ..< GameField.SIZE_IN_TILES.toInt()) {
                if (gameField.walls.occupied(t(x), t(y))) {
                    height++
                    break
                }
                height++
            }
            if (rects.isEmpty() || rects.last().height != height) {
                rects.add(TileRect(t(x), hitBottom + 1, t(1), height))
            } else {
                val last = rects.removeLast()
                rects.add(TileRect(last.x, hitBottom + 1, last.width + 1, height))
            }
        }
        return rects
    }

    private fun calculateLeftFront(hitTop: Tile, hitLeft: Tile, hitBottom: Tile): List<TileRect> {
        val rects = mutableListOf<TileRect>()
        for (y in hitTop.toInt() .. hitBottom.toInt()) {
            var width = t(0)
            for (x in hitLeft.toInt() - 1 downTo 0) {
                if (gameField.walls.occupied(t(x), t(y))) {
                    width++
                    break
                }
                width++
            }
            if (rects.isEmpty() || rects.last().width != width) {
                rects.add(TileRect(hitLeft - width, t(y), width, t(1)))
            } else {
                val last = rects.removeLast()
                rects.add(TileRect(hitLeft - width, last.y, width, last.height + 1))
            }
        }
        return rects
    }

    private fun calculateRightFront(hitTop: Tile, hitRight: Tile, hitBottom: Tile): List<TileRect> {
        val rects = mutableListOf<TileRect>()
        for (y in hitTop.toInt() .. hitBottom.toInt()) {
            var width = t(0)
            for (x in hitRight.toInt() + 1 ..< GameField.SIZE_IN_TILES.toInt()) {
                if (gameField.walls.occupied(t(x), t(y))) {
                    width++
                    break
                }
                width++
            }
            if (rects.isEmpty() || rects.last().width != width) {
                rects.add(TileRect(hitRight + 1, t(y), width, t(1)))
            } else {
                val last = rects.removeLast()
                rects.add(TileRect(hitRight + 1, last.y, width, last.height + 1))
            }
        }
        return rects
    }

    private fun updateTarget() {
        if (target == null && (enemies.isNotEmpty() || powerUp != null)) {
            target = if (powerUp != null) powerUp else enemies.random()
        }
    }

    fun draw(surface: ScreenSurface) {
        if (game.config.debug) {
            tank?.let {
                lineOfSight.forEach { tileRect ->
                    surface.drawRect(
                        tileRect.x.toPixel() + gameField.bounds.x,
                        tileRect.y.toPixel() + gameField.bounds.y,
                        tileRect.width.toPixel(),
                        tileRect.height.toPixel(),
                        ARGB(0x66FF0000)
                    )
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
        tank?.let {
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

    private fun moveTowardsTarget(tankHandle: PlayerTankHandle, t: Sprite) {
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
            else -> Direction.entries.random(random)
        }
        tankHandle.direction = direction
        tankHandle.isIdle = false
    }

    private fun isBaseInLineOfSight(): Boolean {
        return lineOfSight.any { it.intersects(baseRect) }
    }

    fun dispose() {
        tank = null

        strategyTimer.dispose()

        game.eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}