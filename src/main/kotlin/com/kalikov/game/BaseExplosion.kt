package com.kalikov.game

import java.time.Clock

class BaseExplosion(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    clock: Clock
) : Explosion(
    eventManager,
    Animation.pauseAware(eventManager, frameSequenceOf(*animationFrames), clock, ANIMATION_INTERVAL),
    Globals.UNIT_SIZE * 2
) {
    companion object {
        private val animationFrames = intArrayOf(1, 2, 3, 4, 5, 3)

        const val ANIMATION_INTERVAL = 96
    }

    data class Destroyed(val explosion: BaseExplosion) : Event()

    override val image = imageManager.getImage("big_explosion")

    override fun destroyHook() {
        eventManager.fireEvent(Destroyed(this))
    }
}