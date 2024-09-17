package com.kalikov.game

class ShovelHandler(
    private val game: Game,
    private val baseWallBuilder: ShovelWallBuilder,
    solidDuration: Int = SOLID_DURATION,
    blinkDuration: Int = BLINK_DURATION,
    private val blinkCount: Int = 6
) : EventSubscriber {
    companion object {
        const val SOLID_DURATION = 18000
        const val BLINK_DURATION = 256

        private val subscriptions = setOf(PowerUpHandler.ShovelStart::class)
    }

    private val solidTimer = PauseAwareTimer(game.eventManager, game.clock, solidDuration, ::end)
    private val blinkTimer = PauseAwareTimer(game.eventManager, game.clock, blinkDuration, ::blink)

    private var blinkFrame = 0

    init {
        game.eventManager.addSubscriber(this, subscriptions)
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
        baseWallBuilder.buildSteelWall()
    }

    private fun end() {
        solidTimer.stop()
        blinkTimer.restart()
        baseWallBuilder.buildBrickWall()
    }

    private fun blink() {
        if (blinkFrame % 2 == 0) {
            baseWallBuilder.buildSteelWall()
        } else {
            baseWallBuilder.buildBrickWall()
        }
        blinkFrame++
        if (blinkFrame == blinkCount * 2) {
            blinkTimer.stop()
            blinkFrame = 0
        }
    }

    fun update() {
        solidTimer.update()
        blinkTimer.update()
    }

    fun dispose() {
        solidTimer.dispose()
        blinkTimer.dispose()
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}