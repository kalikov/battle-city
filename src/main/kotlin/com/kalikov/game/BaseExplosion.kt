package com.kalikov.game

import com.kalikov.engine.Animation
import com.kalikov.engine.Event
import com.kalikov.engine.FrameSequence
import com.kalikov.engine.Pixel
import com.kalikov.engine.px

class BaseExplosion(
    game: BattleCityGame,
    pauseManager: PauseManager,
    x: Pixel = 0.px,
    y: Pixel = 0.px,
) : Explosion(
    Animation.pauseAware(pauseManager, FrameSequence(animationFrames), game.clock, ANIMATION_INTERVAL),
    SIZE,
    x,
    y,
) {
    companion object {
        val SIZE = 4.tiles.toPixel()

        const val ANIMATION_INTERVAL = 96

        private val animationFrames = intArrayOf(1, 2, 3, 4, 5, 3)
    }

    data class Destroyed(val explosion: BaseExplosion) : Event()

    override val image = game.imageManager.bigExplosion
}