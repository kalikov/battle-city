package com.kalikov.game

import com.kalikov.engine.Animation
import com.kalikov.engine.Event
import com.kalikov.engine.frameSequenceOf

class TankExplosion(
    private val game: BattleCityGame,
    pauseManager: PauseManager,
    val tank: Tank
) : Explosion(
    game.eventManager,
    Animation.pauseAware(pauseManager, frameSequenceOf(*animationFrames), game.clock, 96),
    t(4).toPixel()
) {
    private companion object {
        private val animationFrames = intArrayOf(1, 2, 3, 4, 5, 3)
    }

    data class Destroyed(val explosion: TankExplosion) : Event()

    override val image = game.imageManager.getImage("big_explosion")

    override fun destroyHook() {
        game.eventManager.fireEvent(Destroyed(this))
    }
}