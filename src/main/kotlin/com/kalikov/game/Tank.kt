package com.kalikov.game

sealed class Tank(
    protected val game: Game,
    protected val pauseManager: PauseManager,
    x: Pixel,
    y: Pixel
) : Sprite(
    game.eventManager,
    x,
    y,
    SIZE,
    SIZE,
), AITankHandle, EventSubscriber {
    companion object {
        const val LONG_COOLDOWN_INTERVAL = 200
        const val SHORT_COOLDOWN_INTERVAL = 64

        val SIZE = t(2).toPixel()

        private val subscriptions = setOf(
            Reload::class,
            TankStateAppearing.End::class,
            TankStateInvincible.End::class,
            TankStateFrozen.End::class
        )

        @JvmStatic
        protected fun <T : Tank> init(tank: T): T {
            LeaksDetector.add(tank)

            tank.internalState = TankStateNormal(tank.game.imageManager, tank)
            tank.game.eventManager.addSubscriber(tank, subscriptions)
            return tank
        }

        fun calculateHitRect(bounds: PixelRect): PixelRect {
            var width = bounds.width
            var height = bounds.height
            val dx = bounds.x % Globals.TILE_SIZE
            var x = bounds.x - dx
            if (dx >= Bullet.SIZE / 2) {
                x += Globals.TILE_SIZE
                width -= Globals.TILE_SIZE
            }
            if (dx > Globals.TILE_SIZE - Bullet.SIZE / 2) {
                width += Globals.TILE_SIZE
            }
            val dy = bounds.y % Globals.TILE_SIZE
            var y = bounds.y - dy
            if (dy >= Bullet.SIZE / 2) {
                y += Globals.TILE_SIZE
                height -= Globals.TILE_SIZE
            }
            if (dy > Globals.TILE_SIZE - Bullet.SIZE / 2) {
                height += Globals.TILE_SIZE
            }
            return PixelRect(x, y, width, height)
        }
    }

    data class Shoot(val bullet: Bullet) : Event()
    data class Reload(val tank: Tank) : Event()
    data class Destroyed(val tank: Tank) : Event()
    data class Hit(val tank: Tank) : Event()

    override var isIdle = true

    abstract val image: String
    abstract val imageMod: Int

    val isSlipping get() = !slipCountDown.isStopped

    val canMove get() = state.canMove
    val canBeDestroyed get() = state.canBeDestroyed

    val isCollidable get() = state.isCollidable

    final override var moveFrequency = 6
        set(value) {
            field = value
            moveCountDown = CountDown(value, ::moved)
        }

    var slipDuration = 28
        set(value) {
            field = value
            slipCountDown = CountDown(value)
        }

    override var direction = Direction.RIGHT
        set(value) {
            if (value == field) {
                return
            }
            if (!slipCountDown.isStopped && isSmoothTurnRequired(value)) {
                return
            }
            if (!state.canMove) {
                return
            }
            smoothTurn(value)
            field = value
            updateHitRect()
        }

    private lateinit var internalState: TankState

    var state: TankState
        get() = internalState
        set(value) {
            internalState.dispose()
            internalState = value
        }

    var bulletSpeed = Bullet.Speed.NORMAL
    var bulletsLimit = 1
    private var bullets = 0

    var bulletType = Bullet.Type.REGULAR

    private val longCooldownTimer = PauseAwareTimer(game.eventManager, game.clock, LONG_COOLDOWN_INTERVAL, ::resetLongCooldown)
    private val shortCooldownTimer = PauseAwareTimer(game.eventManager, game.clock, SHORT_COOLDOWN_INTERVAL, ::resetShortCooldown)

    private val turnRoundTo = Globals.TILE_SIZE

    private var moveCountDown = CountDown(moveFrequency, ::moved)
    private var slipCountDown = CountDown(slipDuration)

    final override var hitRect = calculateHitRect(bounds)
        private set

    var moveDistance = 0
        private set

    init {
        z = 1
    }

    private fun updateHitRect() {
        hitRect = calculateHitRect(bounds)
        hitRectHook()
    }

    protected open fun hitRectHook() = Unit

    private fun moved() {
        moveDistance++
    }

    fun startSlipping() {
        if (slipCountDown.isStopped) {
            slipCountDown.restart()
        }
    }

    fun stopSlipping() {
        slipCountDown.stop()
    }

    fun move(movePrecondition: () -> Boolean): Boolean {
        moveCountDown.update()
        if (moveCountDown.isStopped) {
            moveCountDown.restart()
            if (movePrecondition()) {
                slipCountDown.update()
                when (direction) {
                    Direction.RIGHT -> setPosition(x + 1, y)
                    Direction.LEFT -> setPosition(x - 1, y)
                    Direction.UP -> setPosition(x, y - 1)
                    Direction.DOWN -> setPosition(x, y + 1)
                }
                return true
            }
            slipCountDown.stop()
        }
        return false
    }

    private fun createBullet(): Bullet {
        val position = getBulletPosition()
        return Bullet(game, this, bulletSpeed, bulletType, direction, position.x, position.y)
    }

    private fun getBulletPosition(): PixelPoint {
        val x: Pixel
        val y: Pixel
        when (direction) {
            Direction.RIGHT -> {
                x = right + 1
                y = top + height / 2 - Bullet.SIZE / 2
            }

            Direction.LEFT -> {
                x = left - Bullet.SIZE
                y = top + height / 2 - Bullet.SIZE / 2
            }

            Direction.UP -> {
                x = left + width / 2 - Bullet.SIZE / 2
                y = top - Bullet.SIZE
            }

            Direction.DOWN -> {
                x = left + width / 2 - Bullet.SIZE / 2
                y = bottom + 1
            }
        }
        return PixelPoint(x, y)
    }

    override fun shoot() {
        if (isDestroyed || pauseManager.isPaused) {
            return
        }
        if (!state.canShoot) {
            return
        }
        if (bullets >= bulletsLimit) {
            return
        }
        if (shortCooldownTimer.isStopped && bullets > 0 || longCooldownTimer.isStopped) {
            bullets++
            val bullet = createBullet()
            game.eventManager.fireEvent(Shoot(bullet))
            shortCooldownTimer.restart()
            if (bullets == 1) {
                longCooldownTimer.restart()
            }
        }
    }

    private fun resetLongCooldown() {
        longCooldownTimer.stop()
    }

    private fun resetShortCooldown() {
        shortCooldownTimer.stop()
    }

    override fun updateHook() {
        state.update()
        longCooldownTimer.update()
        shortCooldownTimer.update()
    }

    override fun boundsHook() {
        updateHitRect()
    }

    override fun notify(event: Event) {
        if (event is Reload && event.tank === this) {
            bullets--
        } else if (event is TankStateAppearing.End && event.tank === this) {
            stateAppearingEnd()
        } else if (event is TankStateInvincible.End && event.tank === this) {
            state = TankStateNormal(game.imageManager, this)
        } else if (event is TankStateFrozen.End && event.tank === this) {
            state = TankStateNormal(game.imageManager, this)
        }
    }

    abstract fun stateAppearingEnd()

    fun hit(bullet: BulletHandle) {
        if (isDestroyed) {
            return
        }
        game.eventManager.fireEvent(Hit(this))
        hitHook(bullet)
    }

    abstract fun hitHook(bullet: BulletHandle)

    override fun destroyHook() {
        game.eventManager.fireEvent(Destroyed(this))
    }

    override fun dispose() {
        longCooldownTimer.dispose()
        shortCooldownTimer.dispose()

        state.dispose()

        game.eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }

    private fun isSmoothTurnRequired(newDirection: Direction): Boolean {
        val prevDirection = direction
        return if (newDirection.isVertical) {
            prevDirection.isHorizontal && (x % turnRoundTo) > 0
        } else {
            prevDirection.isVertical && (y % turnRoundTo) > 0
        }
    }

    private fun smoothTurn(newDirection: Direction) {
        val prevDirection = direction
        if (newDirection.isVertical) {
            if (prevDirection.isHorizontal) {
                val v = x % turnRoundTo
                if (v > 0) {
                    if (v < turnRoundTo / 2 || v == turnRoundTo / 2 && prevDirection == Direction.LEFT) {
                        setPosition(x - v, y)
                    } else {
                        setPosition(x - v + turnRoundTo, y)
                    }
                }
            }
        } else if (prevDirection.isVertical) {
            val v = y % turnRoundTo
            if (v > 0) {
                if (v < turnRoundTo / 2 || v == turnRoundTo / 2 && prevDirection == Direction.UP) {
                    setPosition(x, y - v)
                } else {
                    setPosition(x, y - v + turnRoundTo)
                }
            }
        }
    }

    override fun draw(surface: ScreenSurface) {
        state.draw(surface)
        if (game.config.debug) {
            surface.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, ARGB(0x6600FF00))
            surface.drawRect(
                hitRect.x,
                hitRect.y,
                hitRect.width,
                hitRect.height,
                ARGB(0x66FF0000)
            )
        }
    }
}