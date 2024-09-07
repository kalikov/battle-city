package com.kalikov.game

import java.time.Clock

class EnemyFactory(
    private val game: Game,
    private val pauseManager: PauseManager,
    private val spriteContainer: SpriteContainer,
    private val positions: List<Point>,
    private val clock: Clock,
    enemies: List<EnemyGroupConfig>,
    interval: Int
) : EventSubscriber {
    data class EnemyCreated(val enemy: EnemyTank, val isFlashing: Boolean) : Event()

    data object LastEnemyDestroyed : Event()

    data object FlashingTankHit : Event()

    companion object {
        private val subscriptions = setOf(TankExplosion.Destroyed::class, Tank.Hit::class)

        val FLASHING_COLORS = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1)
    }

    init {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    var flashingIndices = setOf(4, 11, 18)

    var enemiesToCreateCount: Int
        private set

    var enemyCount = 0
        private set
    var enemyCountLimit = 4
    val enemyCountLimitReached get() = enemyCount >= enemyCountLimit

    private var positionIndex = 0

    private var enemies = emptyArray<EnemyTank.EnemyType>()
    private var enemyIndex = 0

    private val timer = PauseAwareTimer(game.eventManager, clock, interval, ::create)

    private val flashingTanks = HashSet<Tank>(flashingIndices.size)

    init {
        val count = enemies.sumOf { it.count }
        val array = Array(count) { EnemyTank.EnemyType.BASIC }
        var index = 0
        for (enemyGroup in enemies) {
            for (i in 0 until enemyGroup.count) {
                array[index] = enemyGroup.type
                index++
            }
        }
        this.enemies = array
        enemiesToCreateCount = array.size
    }

    fun update() {
        if (pauseManager.isPaused) {
            return
        }

        if (timer.isStopped) {
            create()
        } else {
            timer.update()
        }
    }

    fun nextPosition(): Point {
        val position = positions[positionIndex]
        positionIndex++
        if (positionIndex >= positions.size) {
            positionIndex = 0
        }
        return position
    }

    private fun create() {
        if (enemiesToCreateCount <= 0 || enemyCountLimitReached) {
            timer.stop()
            return
        }
        timer.restart()
        spriteContainer.addSprite(createNextEnemy())
    }

    private fun createNextEnemy(): Tank {
        check(enemiesToCreateCount >= 0)
        val tank = createEnemy(nextEnemy(), nextPosition())
        enemyCount++
        val total = enemies.size
        val index = total - enemiesToCreateCount
        val isFlashing = flashingIndices.contains(index + 1)
        if (isFlashing) {
            tank.color.colors[0] = FLASHING_COLORS
            flashingTanks.add(tank)
        }
        enemiesToCreateCount--
        game.eventManager.fireEvent(EnemyCreated(tank, isFlashing))
        return tank
    }

    private fun createEnemy(type: EnemyTank.EnemyType, position: Point): EnemyTank {
        val tank = EnemyTank.create(game, pauseManager, clock, position.x, position.y, type)
        tank.state = TankStateAppearing(game.eventManager, game.imageManager, tank)

        when (type) {
            EnemyTank.EnemyType.BASIC -> {
                tank.moveFrequency = 8
            }

            EnemyTank.EnemyType.FAST -> {
                tank.moveFrequency = 4
            }

            EnemyTank.EnemyType.POWER -> {
                tank.moveFrequency = 8
                tank.bulletSpeed = Bullet.Speed.FAST
            }

            EnemyTank.EnemyType.ARMOR -> {
                tank.moveFrequency = 8
                tank.hitLimit = 4
                tank.color.colors = arrayOf(intArrayOf(0, 2), intArrayOf(0, 3), intArrayOf(2, 3), intArrayOf(0))
            }
        }
        return tank
    }

    fun nextEnemy(): EnemyTank.EnemyType {
        val type = enemies[enemyIndex]
        enemyIndex++
        if (enemyIndex >= enemies.size) {
            enemyIndex = 0
        }
        return type
    }

    override fun notify(event: Event) {
        if (event is TankExplosion.Destroyed) {
            if (event.explosion.tank is EnemyTank) {
                enemyCount--
            }
            if (event.explosion.tank is EnemyTank && enemyCount <= 0 && enemiesToCreateCount == 0) {
                game.eventManager.fireEvent(LastEnemyDestroyed)
            }
        } else if (event is Tank.Hit) {
            if (flashingTanks.remove(event.tank)) {
                game.eventManager.fireEvent(FlashingTankHit)
            }
        }
    }

    fun dispose() {
        timer.dispose()

        game.eventManager.removeSubscriber(this, subscriptions)
    }
}