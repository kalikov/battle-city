package com.kalikov.game

import com.kalikov.engine.Animation
import com.kalikov.engine.FrameSequence

class BulletExplosion(
    game: BattleCityGame,
    pauseManager: PauseManager,
    val bullet: BulletHandle,
) : Explosion(
    Animation.pauseAware(pauseManager, FrameSequence(animationFrames), game.clock, ANIMATION_INTERVAL),
    SIZE,
    bullet.center - SIZE / 2,
    bullet.middle - SIZE / 2
) {
    companion object {
        private val animationFrames = intArrayOf(1, 2, 3)

        const val ANIMATION_INTERVAL = 32
        val ANIMATION_FRAMES = animationFrames.size

        val SIZE = 2.tiles.toPixel()
    }

    override val image = game.imageManager.bulletExplosion
}