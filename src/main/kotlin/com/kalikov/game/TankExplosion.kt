package com.kalikov.game

class TankExplosion(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    val tank: Tank
) : Explosion(
    eventManager,
    Animation.pauseAware(eventManager, frameSequenceOf(1, 2, 3, 4, 5, 3), tank.clock, 64),
    Globals.UNIT_SIZE * 2
) {
    data class Destroyed(val explosion: TankExplosion) : Event()

    init {
        LeaksDetector.add(this)
    }

    override val image = imageManager.getImage("big_explosion")

    override fun destroyHook() {
        eventManager.fireEvent(Destroyed(this))
    }

    override fun dispose() {
        LeaksDetector.remove(this)
    }
}