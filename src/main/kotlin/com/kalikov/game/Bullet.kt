package com.kalikov.game

class Bullet(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    val tank: Tank,
    val speed: Speed
) : Sprite(eventManager, 0, 0, BULLET_SIZE, BULLET_SIZE) {
    internal companion object {
        internal const val BULLET_SIZE = Globals.TILE_SIZE / 2
    }

    data class Destroyed(val bullet: Bullet) : Event()

    enum class Speed(val value: Int, val frequency: Int) {
        NORMAL(1, 2),
        FAST(2, 1)
    }

    enum class Type {
        REGULAR,
        ENHANCED
    }

    var type = Type.REGULAR

    val moveCountDown = CountDown(speed.frequency)

    var direction = Direction.RIGHT

    var shouldExplode = true
        private set

    private val image = imageManager.getImage("bullet")

    init {
        z = 2

        moveCountDown.restart()
    }

    fun hit(shouldExplode: Boolean) {
        this.shouldExplode = shouldExplode
        destroy()
    }

    override fun destroyHook() {
        eventManager.fireEvent(Destroyed(this))
    }

    override fun dispose() {
    }

    override fun draw(surface: ScreenSurface) {
        surface.draw(x, y, image, direction.index * width, 0, width, height)
    }

    fun outOfBounds() {
        if (tank.isPlayer) {
            eventManager.fireEvent(SoundManager.Play("bullet_hit_1"))
        }
        destroy()
    }
}