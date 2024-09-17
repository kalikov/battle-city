package com.kalikov.game

class BaseExplosion(
    private val game: Game,
    x: Pixel = px(0),
    y: Pixel = px(0),
) : Explosion(
    game.eventManager,
    Animation.pauseAware(game.eventManager, frameSequenceOf(*animationFrames), game.clock, ANIMATION_INTERVAL),
    SIZE,
    x,
    y,
) {
    companion object {
        val SIZE = t(4).toPixel()

        const val ANIMATION_INTERVAL = 96

        private val animationFrames = intArrayOf(1, 2, 3, 4, 5, 3)
    }

    data class Destroyed(val explosion: BaseExplosion) : Event()

    override val image = game.imageManager.getImage("big_explosion")

    override fun destroyHook() {
        game.eventManager.fireEvent(Destroyed(this))
    }
}