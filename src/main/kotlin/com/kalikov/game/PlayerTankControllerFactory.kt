package com.kalikov.game

class PlayerTankControllerFactory(
    private val eventManager: EventManager,
    private val pauseManager: PauseManager
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(PlayerTankFactory.PlayerTankCreated::class)
    }

    var controller: PlayerTankController? = null
        private set

    init {
        LeaksDetector.add(this)

        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is PlayerTankFactory.PlayerTankCreated) {
            controller?.dispose()
            controller = null
            controller = create(event.tank)
        }
    }

    private fun create(tank: Tank): PlayerTankController {
        return PlayerTankController(eventManager, pauseManager, tank)
    }

    fun dispose() {
        controller?.dispose()
        controller = null

        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}