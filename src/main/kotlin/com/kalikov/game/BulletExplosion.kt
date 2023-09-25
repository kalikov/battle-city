package com.kalikov.game

import java.time.Clock

class BulletExplosion(
    eventManager: EventManager,
    imageManager: ImageManager,
    clock: Clock
) : Explosion(
    eventManager,
    Animation.pauseAware(eventManager, frameSequenceOf(1, 2, 3), clock, ANIMATION_INTERVAL),
    Globals.UNIT_SIZE
) {
    companion object {
        const val ANIMATION_INTERVAL = 32
    }

    override val image = imageManager.getImage("bullet_explosion")

    override fun dispose() {
    }
}