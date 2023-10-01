package com.kalikov.game

class Bullet(
    private val eventManager: EventManager,
    imageManager: ImageManager,
    val tank: Tank,
    val speed: Speed
) : Sprite(eventManager, 0, 0, SIZE, SIZE) {
    internal companion object {
        internal const val SIZE = Globals.TILE_SIZE / 2
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

    fun move(): Boolean {
        moveCountDown.update()
        if (moveCountDown.stopped) {
            moveCountDown.restart()
            when (direction) {
                Direction.RIGHT -> setPosition(x + 1, y)
                Direction.LEFT -> setPosition(x - 1, y)
                Direction.UP -> setPosition(x, y - 1)
                Direction.DOWN -> setPosition(x, y + 1)
            }
            return true
        }
        return false
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