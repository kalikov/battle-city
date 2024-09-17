package com.kalikov.game

class PlayerTankControllerFactory(
    private val eventManager: EventManager,
    private val pauseManager: PauseManager,
    val player: Player,
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(PlayerTankFactory.PlayerTankCreated::class)
    }

    var controller: PlayerTankController? = null
        private set

    private var isActive = true

    init {
        LeaksDetector.add(this)

        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is PlayerTankFactory.PlayerTankCreated) {
            if (event.tank.player === player) {
                controller?.dispose()
                controller = null
                controller = create(event.tank)
            }
        }
    }

    private fun create(tank: PlayerTank): PlayerTankController {
        return PlayerTankController(eventManager, pauseManager, tank)
    }

    fun update() {
        controller?.update()
    }

    fun dispose() {
        controller?.dispose()
        controller = null

        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}