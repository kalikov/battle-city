package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventManager
import com.kalikov.engine.EventSubscriber
import kotlin.random.Random

class AITankControllerContainer(
    private val eventManager: EventManager,
    private val pauseManager: PauseManager,
    private val base: PixelPoint,
    private val random: Random = Random.Default,
    private val params: AITankControllerParams = AITankControllerParams()
) : EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf(
            Tank.Destroyed::class,
            EnemyFactory.EnemyCreated::class,
            PowerUpHandler.Freeze::class,
            FreezeHandler.Unfreeze::class,
            PlayerTankFactory.PlayerTankCreated::class,
            PlayerTank.PlayerDestroyed::class,
        )
    }

    var isFrozen = false
        private set

    private val controllers = LinkedHashMap<Tank, AITankController>()

    private val players = mutableSetOf<PlayerTankHandle>()
    override val identity: Int
        get() = TODO("Not yet implemented")

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        when (event) {
            is EnemyFactory.EnemyCreated -> controllers[event.enemy] = createController(event.enemy)

            is PowerUpHandler.Freeze -> freeze()

            is FreezeHandler.Unfreeze -> unfreeze()

            is Tank.Destroyed -> controllers.remove(event.tank)?.dispose()

            is PlayerTankFactory.PlayerTankCreated -> players.add(event.tank)

            is PlayerTank.PlayerDestroyed -> players.remove(event.tank)

            else -> Unit
        }
    }

    fun update() {
        if (isFrozen || pauseManager.isPaused) {
            return
        }
        controllers.values.forEach {
            it.update()
        }
    }

    fun hasController(tank: Tank): Boolean {
        return controllers.contains(tank)
    }

    private fun createController(tank: Tank): AITankController {
        val controller = AITankController(pauseManager, tank, base, players, random, params)
        if (isFrozen) {
            tank.isIdle = true
        }
        return controller
    }

    private fun freeze() {
        isFrozen = true
        controllers.keys.forEach { tank ->
            tank.isIdle = true
        }
    }

    private fun unfreeze() {
        isFrozen = false
        controllers.keys.forEach { tank ->
            tank.isIdle = false
        }
    }

    fun dispose() {
        controllers.values.forEach { it.dispose() }
        controllers.clear()

        eventManager.removeSubscriber(this, subscriptions)
    }
}