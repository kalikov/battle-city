package com.kalikov.game

import java.time.Clock

class BulletExplosion(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    clock: Clock,
    private val bullet: Bullet,
) : Explosion(
    eventManager,
    Animation.pauseAware(eventManager, frameSequenceOf(*animationFrames), clock, ANIMATION_INTERVAL),
    Globals.UNIT_SIZE
) {
    companion object {
        private val animationFrames = intArrayOf(1, 2, 3)

        const val ANIMATION_INTERVAL = 32
    }

    override val image = imageManager.getImage("bullet_explosion")

    override fun destroyHook() {
        eventManager.fireEvent(Tank.Reload(bullet.tank))
    }
}