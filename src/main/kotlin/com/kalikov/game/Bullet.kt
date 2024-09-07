package com.kalikov.game

class Bullet(
    private val game: Game,
    override val tank: Tank,
    val speed: Speed
) : BulletHandle, Sprite(game.eventManager, 0, 0, SIZE, SIZE) {
    internal companion object {
        internal const val SIZE = Globals.TILE_SIZE / 2
    }

    data class Exploded(val bullet: Bullet) : Event()

    enum class Speed(val value: Int, val frequency: Int) {
        NORMAL(1, 2),
        FAST(2, 1)
    }

    enum class Type {
        REGULAR,
        ENHANCED
    }

    var type = Type.REGULAR

    var direction = Direction.RIGHT

    private val moveCountDown = CountDown(speed.frequency)

    var shouldExplode = true
        private set

    private val image = game.imageManager.getImage("bullet")

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
        if (moveCountDown.isStopped) {
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
        if (shouldExplode) {
            game.eventManager.fireEvent(Exploded(this))
        } else {
            game.eventManager.fireEvent(Tank.Reload(tank))
        }
    }

    override fun dispose() {
    }

    override fun draw(surface: ScreenSurface) {
        surface.draw(x, y, image, direction.index * width, 0, width, height)
        if (game.config.debug) {
            surface.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, ARGB(0x6600FF00))
        }
    }

    fun outOfBounds() {
        if (tank is PlayerTank) {
            game.eventManager.fireEvent(SoundManager.Play("bullet_hit_1"))
        }
        destroy()
    }
}