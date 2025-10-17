package com.kalikov.game

import com.kalikov.engine.Animation
import com.kalikov.engine.Event
import com.kalikov.engine.Pixel
import com.kalikov.engine.frameSequenceOf
import com.kalikov.engine.px

class BaseExplosion(
    private val game: BattleCityGame,
    pauseManager: PauseManager,
    x: Pixel = px(0),
    y: Pixel = px(0),
) : Explosion(
    game.eventManager,
    Animation.pauseAware(pauseManager, frameSequenceOf(*animationFrames), game.clock, ANIMATION_INTERVAL),
    SIZE,
    x,
    y,
) {
    companion object {
        val SIZE = t(4).toPixel()

        const val ANIMATION_INTERVAL = 96

        private val animationFrames = intArrayOf(1, 2, 3, 4, 5, 3)
    }

    data class Destroyed(val explosion: BaseExplosion) : Event()

    override val image = game.imageManager.getImage("big_explosion")

    override fun destroyHook() {
        game.eventManager.fireEvent(Destroyed(this))
    }
}