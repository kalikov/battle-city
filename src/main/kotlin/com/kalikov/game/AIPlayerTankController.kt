package com.kalikov.game

class AIPlayerTankController(
    private val eventManager: EventManager,
    private val player: Player
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            PlayerTankFactory.PlayerTankCreated::class,
            BaseExplosion.Destroyed::class,
            Keyboard.KeyPressed::class,
            Keyboard.KeyReleased::class,
        )
    }

    private var tank: AITankHandle? = null

    private var isActive = true

    init {
        LeaksDetector.add(this)

        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        when (event) {
            is PlayerTankFactory.PlayerTankCreated -> {
                if (event.tank.player === player) {
                    tank = event.tank
                }
            }

            is BaseExplosion.Destroyed -> {
                tank?.let {
                    it.isIdle = true
                }
                isActive = false
            }

            else -> Unit
        }
    }

    fun update() {

    }

    fun dispose() {
        tank = null

        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}