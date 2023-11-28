package com.kalikov.game

import java.time.Clock

class EnemyFactory(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val pauseManager: PauseManager,
    private val spriteContainer: SpriteContainer,
    private val positions: List<Point>,
    private val clock: Clock,
    enemies: List<EnemyGroupConfig>,
    interval: Int
) : EventSubscriber {
    data class EnemyCreated(val enemy: Tank) : Event()

    data object LastEnemyDestroyed : Event()

    private companion object {
        private val subscriptions = setOf(TankExplosion.Destroyed::class)
    }

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    var flashingTanks = setOf(4, 11, 18)

    val position get() = positions[positionIndex]

    val enemy get() = enemies[enemyIndex]
    val enemiesToCreateCount get() = enemies.size - enemyIndex

    var enemyCount = 0
        private set
    var enemyCountLimit = 4
    val enemyCountLimitReached get() = enemyCount >= enemyCountLimit

    private var positionIndex = 0

    private var enemies = emptyArray<Tank.EnemyType>()
    private var enemyIndex = 0

    private val timer = PauseAwareTimer(eventManager, clock, interval, ::create)

    init {
        val count = enemies.sumOf { it.count }
        val array = Array(count) { Tank.EnemyType.BASIC }
        var index = 0
        for (enemyGroup in enemies) {
            for (i in 0 until enemyGroup.count) {
                array[index] = enemyGroup.type
                index++
            }
        }
        this.enemies = array
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

    fun nextPosition() {
        positionIndex++
        if (positionIndex >= positions.size) {
            positionIndex = 0
        }
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
        val tank = createEnemy(enemy, position)
        nextEnemy()
        nextPosition()
        return tank
    }

    private fun createEnemy(type: Tank.EnemyType, position: Point): Tank {
        val tank = Tank(eventManager, pauseManager, imageManager, clock, position.x, position.y)
        tank.enemyType = type
        tank.state = TankStateAppearing(eventManager, imageManager, tank)

        when (type) {
            Tank.EnemyType.BASIC -> {
                tank.moveFrequency = 8
            }

            Tank.EnemyType.FAST -> {
                tank.moveFrequency = 4
            }

            Tank.EnemyType.POWER -> {
                tank.moveFrequency = 8
                tank.bulletSpeed = Bullet.Speed.FAST
            }

            Tank.EnemyType.ARMOR -> {
                tank.moveFrequency = 8
                tank.hitLimit = 4
                tank.color.colors = arrayOf(intArrayOf(0, 1), intArrayOf(0, 2), intArrayOf(1, 2), intArrayOf(0))
            }
        }

        if (flashingTanks.contains(enemyIndex + 1)) {
            tank.isFlashing = true
        }

        enemyCount++
        eventManager.fireEvent(EnemyCreated(tank))

        return tank
    }

    fun nextEnemy() {
        enemyIndex++
    }

    override fun notify(event: Event) {
        if (event is TankExplosion.Destroyed) {
            if (event.explosion.tank.isEnemy) {
                enemyCount--
            }
            if (event.explosion.tank.isEnemy && enemyCount <= 0 && enemiesToCreateCount == 0) {
                eventManager.fireEvent(LastEnemyDestroyed)
            }
        }
    }

    fun dispose() {
        timer.dispose()

        eventManager.removeSubscriber(this, subscriptions)
    }
}