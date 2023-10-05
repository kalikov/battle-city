package com.kalikov.game

class TankExplosion(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    val tank: Tank
) : Explosion(
    eventManager,
    Animation.pauseAware(eventManager, frameSequenceOf(*animationFrames), tank.clock, 96),
    Globals.UNIT_SIZE * 2
) {
    private companion object {
        private val animationFrames = intArrayOf(1, 2, 3, 4, 5, 3)
    }

    data class Destroyed(val explosion: TankExplosion) : Event()

    override val image = imageManager.getImage("big_explosion")

    override fun destroyHook() {
        eventManager.fireEvent(Destroyed(this))
    }
}