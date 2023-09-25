package com.kalikov.game

import java.time.Clock

class ShovelHandler(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val baseWallBuilder: ShovelWallBuilder,
    clock: Clock,
    duration: Int = SHOVEL_DURATION
) : EventSubscriber {
    companion object {
        const val SHOVEL_DURATION = 9000

        private val subscriptions = setOf(PowerUpHandler.ShovelStart::class)
    }

    private val timer = PauseAwareTimer(eventManager, clock, duration, ::end)

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is PowerUpHandler.ShovelStart) {
            start()
        }
    }

    private fun start() {
        timer.restart()
        rebuildWall(SteelWallFactory(eventManager, imageManager))
    }

    private fun end() {
        timer.stop()
        rebuildWall(BrickWallFactory(eventManager, imageManager))
    }

    private fun rebuildWall(wallFactory: WallFactory) {
        baseWallBuilder.destroyWall()
        baseWallBuilder.buildWall(wallFactory)
    }

    fun update() {
        timer.update()
    }
    
    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}