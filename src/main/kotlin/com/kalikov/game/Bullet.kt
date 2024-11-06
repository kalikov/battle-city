package com.kalikov.game

class Bullet(
    private val game: Game,
    override val tank: Tank,
    val speed: Speed = Speed.NORMAL,
    var type: Type = Type.REGULAR,
    var direction: Direction = Direction.RIGHT,
    x: Pixel = px(0),
    y: Pixel = px(0),
) : BulletHandle, Sprite(game.eventManager, x, y, SIZE, SIZE) {
    companion object {
        val SIZE = t(1).toPixel() / 2
    }

    data class Exploded(val bullet: BulletHandle) : Event()

    enum class Speed(val value: Int, val frequency: Int) {
        NORMAL(1, 2),
        FAST(2, 1)
    }

    enum class Type {
        REGULAR,
        ENHANCED
    }

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
        surface.draw(x, y, image, direction.index * width, px(0), width, height)
        if (game.config.debug) {
            surface.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, ARGB(0x6600FF00))
        }
    }

    fun outOfBounds() {
        if (tank is PlayerTank) {
            game.soundManager.bulletHitSteel.play()
        }
        destroy()
    }
}