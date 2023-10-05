package com.kalikov.game

import java.time.Clock

class ShovelHandler(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    private val baseWallBuilder: ShovelWallBuilder,
    clock: Clock,
    solidDuration: Int = SOLID_DURATION,
    blinkDuration: Int = BLINK_DURATION,
    private val blinkCount: Int = 6
) : EventSubscriber {
    companion object {
        const val SOLID_DURATION = 18000
        const val BLINK_DURATION = 256

        private val subscriptions = setOf(PowerUpHandler.ShovelStart::class)
    }

    private val solidTimer = PauseAwareTimer(eventManager, clock, solidDuration, ::end)
    private val blinkTimer = PauseAwareTimer(eventManager, clock, blinkDuration, ::blink)

    private val steelWallFactory = SteelWallFactory(eventManager, imageManager)
    private val brickWallFactory = BrickWallFactory(eventManager, imageManager)

    private var blinkFrame = 0

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is PowerUpHandler.ShovelStart) {
            start()
        }
    }

    private fun start() {
        blinkTimer.stop()
        solidTimer.restart()
        blinkFrame = 0
        rebuildWall(steelWallFactory)
    }

    private fun end() {
        solidTimer.stop()
        blinkTimer.restart()
        rebuildWall(brickWallFactory)
    }

    private fun blink() {
        if (blinkFrame % 2 == 0) {
            rebuildWall(steelWallFactory)
        } else {
            rebuildWall(brickWallFactory)
        }
        blinkFrame++
        if (blinkFrame == blinkCount * 2) {
            blinkTimer.stop()
            blinkFrame = 0
        }
    }

    private fun rebuildWall(wallFactory: WallFactory) {
        baseWallBuilder.destroyWall()
        baseWallBuilder.buildWall(wallFactory)
    }

    fun update() {
        solidTimer.update()
        blinkTimer.update()
    }

    fun dispose() {
        solidTimer.dispose()
        blinkTimer.dispose()
        eventManager.removeSubscriber(this, subscriptions)
    }
}