package com.kalikov.game

class TankExplosion(
    private val game: Game,
    val tank: Tank
) : Explosion(
    game.eventManager,
    Animation.pauseAware(game.eventManager, frameSequenceOf(*animationFrames), game.clock, 96),
    Globals.UNIT_SIZE * 2
) {
    private companion object {
        private val animationFrames = intArrayOf(1, 2, 3, 4, 5, 3)
    }

    data class Destroyed(val explosion: TankExplosion) : Event()

    override val image = game.imageManager.getImage("big_explosion")

    override fun destroyHook() {
        game.eventManager.fireEvent(Destroyed(this))
    }
}