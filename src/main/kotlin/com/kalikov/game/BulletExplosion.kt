package com.kalikov.game

import com.kalikov.engine.Animation
import com.kalikov.engine.frameSequenceOf

class BulletExplosion(
    private val game: BattleCityGame,
    pauseManager: PauseManager,
    private val bullet: BulletHandle,
) : Explosion(
    game.eventManager,
    Animation.pauseAware(pauseManager, frameSequenceOf(*animationFrames), game.clock, ANIMATION_INTERVAL),
    SIZE,
    bullet.center - SIZE / 2,
    bullet.middle - SIZE / 2
) {
    companion object {
        private val animationFrames = intArrayOf(1, 2, 3)

        const val ANIMATION_INTERVAL = 32
        val ANIMATION_FRAMES = animationFrames.size

        val SIZE = t(2).toPixel()
    }

    override val image = game.imageManager.getImage("bullet_explosion")

    override fun destroyHook() {
        game.eventManager.fireEvent(Tank.Reload(bullet.tank))
    }
}