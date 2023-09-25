package com.kalikov.game

import java.time.Clock

class BaseExplosion(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    clock: Clock
) : Explosion(
    eventManager,
    Animation.pauseAware(eventManager, frameSequenceOf(1, 2, 3, 4, 5, 3), clock, ANIMATION_INTERVAL),
    Globals.UNIT_SIZE * 2
) {
    companion object {
        const val ANIMATION_INTERVAL = 64
    }

    data class Destroyed(val explosion: BaseExplosion) : Event()

    override val image = imageManager.getImage("big_explosion")

    override fun destroyHook() {
        eventManager.fireEvent(Destroyed(this))
    }

    override fun dispose() {
    }
}