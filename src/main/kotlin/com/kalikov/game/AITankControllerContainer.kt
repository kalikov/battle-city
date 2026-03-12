package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import kotlin.random.Random

class AITankControllerContainer(
    private val game: BattleCityGame,
    private val pauseManager: PauseManager,
    private val playerTanksManager: PlayerTanksManager,
    private val gameFieldBounds: PixelRect,
    private val random: Random = Random.Default,
    private val params: AITankControllerParams = AITankControllerParams()
) : EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf(
            Tank.Destroyed::class,
            GameEnemyTanksManager.EnemyCreated::class,
            PowerUpManager.Freeze::class,
            FreezeHandler.Unfreeze::class,
        )
    }

    override val identity get() = Globals.IDENTITY_AI_ENEMY_CONTROLLER

    var isFrozen = false
        private set

    private val controllers = LinkedHashMap<Tank, AITankController>()

    fun activate() {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    fun deactivate() {
        game.eventManager.removeSubscriber(this, subscriptions)

        isFrozen = false

        controllers.values.forEach { it.dispose() }
        controllers.clear()
    }

    override fun notify(event: Event) {
        when (event) {
            is GameEnemyTanksManager.EnemyCreated -> controllers[event.enemy] = createController(event.enemy)

            is PowerUpManager.Freeze -> freeze()

            is FreezeHandler.Unfreeze -> unfreeze()

            is Tank.Destroyed -> controllers.remove(event.tank)?.dispose()

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
        val base = game.stageManager.stageMap.base
        val controller = AITankController(
            pauseManager,
            playerTanksManager,
            tank,
            base.toPixelPoint().translate(gameFieldBounds.x, gameFieldBounds.y),
            random,
            params,
        )
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
    }
}