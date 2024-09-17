package com.kalikov.game

class BulletExplosion(
    private val game: Game,
    private val bullet: BulletHandle,
) : Explosion(
    game.eventManager,
    Animation.pauseAware(game.eventManager, frameSequenceOf(*animationFrames), game.clock, ANIMATION_INTERVAL),
    SIZE,
    bullet.center - SIZE / 2,
    bullet.middle - SIZE / 2
) {
    companion object {
        private val animationFrames = intArrayOf(1, 2, 3)

        const val ANIMATION_INTERVAL = 32

        val SIZE = t(2).toPixel()
    }

    override val image = game.imageManager.getImage("bullet_explosion")

    override fun destroyHook() {
        game.eventManager.fireEvent(Tank.Reload(bullet.tank))
    }
}