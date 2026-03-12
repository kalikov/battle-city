package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.ScreenSurface
import com.kalikov.game.Tank.Destroyed
import com.kalikov.util.ArraySet
import kotlin.reflect.KClass

class GameEnemyTanksManager(
    private val game: BattleCityGame,
    private val pauseManager: PauseManager,
    private val gameFieldBounds: PixelRect,
) : EnemyTanksManager, EventSubscriber {
    data class EnemyCreated(val enemy: EnemyTank) : Event()

    data object LastEnemyDestroyed : Event()

    companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(
            TankExplosion.Destroyed::class,
            TankStateAppearing.End::class,
            TankStateInvincible.End::class,
            TankStateFrozen.End::class,
        )
    }

    override val identity get() = Globals.IDENTITY_ENEMY_MANAGER

    private val flashingIndices = setOf(4, 11, 18)

    var enemiesToCreateCount: Int = 0
        private set

    var enemyCount = 0
        private set
    var enemyCountLimit = 4
    val enemyCountLimitReached get() = enemyCount >= enemyCountLimit

    private val enemies = Array(32) { EnemyTank.EnemyType.BASIC }
    private var enemyIndex = 0

    private val timer = PauseAwareTimer(pauseManager, game.clock, 32, ::create)

    private val tanks = ArraySet<EnemyTank>(Sprite.ID_ORDER)

    fun activate() {
        game.eventManager.addSubscriber(this, subscriptions)

        val count = game.stageManager.stageEnemies.sumOf { it.count }
        var index = 0
        for (enemyGroup in game.stageManager.stageEnemies) {
            repeat(enemyGroup.count) {
                enemies[index] = enemyGroup.type
                index++
            }
        }
        enemyIndex = 0
        enemiesToCreateCount = count
    }

    fun deactivate() {
        timer.stop()

        game.eventManager.removeSubscriber(this, subscriptions)

        tanks.forEach {
            it.dispose()
        }
        tanks.clear()
        enemyCount = 0
    }

    override fun forEach(action: (EnemyTank) -> Unit) {
        tanks.forEach(action)
    }

    override fun iterateWhile(predicate: (EnemyTank) -> Boolean): Boolean {
        return tanks.iterateWhile(predicate)
    }

    override fun update() {
        tanks.removeIf {
            if (it.isDestroyed) {
                game.eventManager.fireEvent(Destroyed(it))
                if (it.isValued) {
                    game.soundManager.enemyExplosion.play()
                }
            }
            it.update()
        }

        if (pauseManager.isPaused) {
            return
        }

        if (timer.isStopped) {
            create()
        } else {
            timer.update()
        }
    }

    override fun draw(surface: ScreenSurface) {
        tanks.forEach {
            if (!it.isDestroyed) {
                it.draw(surface)
            }
        }
    }

    private fun create() {
        if (enemiesToCreateCount <= 0 || enemyCountLimitReached) {
            timer.stop()
            return
        }
        timer.restart(game.stageManager.stageEnemySpawnDelay)
        tanks.add(createNextEnemy())
    }

    private fun createNextEnemy(): EnemyTank {
        check(enemiesToCreateCount >= 0)

        val isFlashing = flashingIndices.contains(enemyIndex + 1)

        val positions = game.stageManager.stageMap.enemySpawnPoints
        val position = positions[enemyIndex % positions.size]
        val tank = createEnemy(enemies[enemyIndex], position, isFlashing)
        enemyCount++
        enemiesToCreateCount--
        enemyIndex++
        game.eventManager.fireEvent(EnemyCreated(tank))
        return tank
    }

    private fun createEnemy(type: EnemyTank.EnemyType, position: TilePoint, isFlashing: Boolean): EnemyTank {
        val x = position.x.toPixel() + gameFieldBounds.x
        val y = position.y.toPixel() + gameFieldBounds.y
        val tank = EnemyTank.create(game, pauseManager, x, y, type, isFlashing)
        tank.state = TankStateAppearing(game, pauseManager, tank)
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
            }
        }
        return tank
    }

    override fun notify(event: Event) {
        if (event is TankExplosion.Destroyed) {
            if (event.explosion.tank is EnemyTank) {
                enemyCount--
            }
            if (event.explosion.tank is EnemyTank && enemyCount <= 0 && enemiesToCreateCount == 0) {
                game.eventManager.fireEvent(LastEnemyDestroyed)
            }
        } else if (event is TankStateAppearing.End && event.tank is EnemyTank) {
            event.tank.state = TankStateNormal(game.imageManager, event.tank)
            event.tank.direction = Direction.DOWN
        } else if (event is TankStateInvincible.End && event.tank is EnemyTank) {
            event.tank.state = TankStateNormal(game.imageManager, event.tank)
        } else if (event is TankStateFrozen.End && event.tank is EnemyTank) {
            event.tank.state = TankStateNormal(game.imageManager, event.tank)
        }
    }

    fun dispose() {
        tanks.forEach {
            it.dispose()
        }
        tanks.clear()
    }
}