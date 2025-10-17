package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import kotlin.reflect.KClass

class ShovelHandler(
    private val game: BattleCityGame,
    pauseManager: PauseManager,
    private val baseWallBuilder: ShovelWallBuilder,
    solidDuration: Int = SOLID_DURATION,
    blinkDuration: Int = BLINK_DURATION,
    private val blinkCount: Int = 6
) : EventSubscriber {
    companion object {
        const val SOLID_DURATION = 18000
        const val BLINK_DURATION = 256

        private val subscriptions = arrayOf<KClass<out Event>>(PowerUpHandler.ShovelStart::class)
    }

    private val solidTimer = PauseAwareTimer(pauseManager, game.clock, solidDuration, ::end)
    private val blinkTimer = PauseAwareTimer(pauseManager, game.clock, blinkDuration, ::blink)

    private var blinkFrame = 0
    override val identity: Int
        get() = TODO("Not yet implemented")

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
        solidTimer.stop()
        blinkTimer.stop()
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}