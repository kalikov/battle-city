package com.kalikov.game

import java.time.Clock

sealed class Tank(
    protected val eventManager: EventManager,
    protected val pauseManager: PauseManager,
    protected val imageManager: ImageManager,
    val clock: Clock,
    x: Int,
    y: Int
) : Sprite(
    eventManager,
    x,
    y,
    SIZE,
    SIZE
), AITankHandle, EventSubscriber {
    companion object {
        const val COOLDOWN_INTERVAL = 200
        const val SIZE = Globals.UNIT_SIZE

        private val subscriptions = setOf(
            Bullet.Destroyed::class,
            TankStateAppearing.End::class,
            TankStateInvincible.End::class,
            TankStateFrozen.End::class
        )

        @JvmStatic
        protected fun <T : Tank> init(tank: T): T {
            LeaksDetector.add(tank)

            tank.internalState = TankStateNormal(tank.imageManager, tank)
            tank.eventManager.addSubscriber(tank, subscriptions)
            return tank
        }
    }

    data class Shoot(val tank: Tank) : Event()
    data class Destroyed(val tank: Tank) : Event()
    data class Hit(val tank: Tank) : Event()

    override var isIdle = true

    abstract val image: String
    abstract val imageMod: Int

    val isSlipping get() = !slipCountDown.isStopped

    val canMove get() = state.canMove
    val canBeDestroyed get() = state.canBeDestroyed

    val isCollidable get() = state.isCollidable

    var moveFrequency = 6
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

    private val cooldownTimer = PauseAwareTimer(eventManager, clock, COOLDOWN_INTERVAL, ::resetCooldown)

    private val turnRoundTo = Globals.TILE_SIZE

    private var moveCountDown = CountDown(moveFrequency, ::moved)
    private var slipCountDown = CountDown(slipDuration)

    var moveDistance = 0
        private set

    init {
        z = 1
    }

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

    fun createBullet(): Bullet {
        val bullet = Bullet(eventManager, imageManager, this, bulletSpeed)
        bullet.setPosition(getBulletPosition(bullet))
        bullet.direction = direction
        bullet.type = bulletType
        return bullet
    }

    private fun getBulletPosition(bullet: Bullet): Point {
        val x: Int
        val y: Int
        when (bullet.tank.direction) {
            Direction.RIGHT -> {
                x = bullet.tank.right + 1
                y = bullet.tank.top + bullet.tank.height / 2 - bullet.height / 2
            }

            Direction.LEFT -> {
                x = bullet.tank.left - bullet.width
                y = bullet.tank.top + bullet.tank.height / 2 - bullet.height / 2
            }

            Direction.UP -> {
                x = bullet.tank.left + bullet.tank.width / 2 - bullet.width / 2
                y = bullet.tank.top - bullet.height
            }

            Direction.DOWN -> {
                x = bullet.tank.left + bullet.tank.width / 2 - bullet.width / 2
                y = bullet.tank.bottom + 1
            }
        }
        return Point(x, y)
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
        if (cooldownTimer.isStopped) {
            bullets++
            eventManager.fireEvent(Shoot(this))
            cooldownTimer.restart()
        }
    }

    private fun resetCooldown() {
        cooldownTimer.stop()
    }

    override fun updateHook() {
        state.update()
        cooldownTimer.update()
    }

    override fun notify(event: Event) {
        if (event is Bullet.Destroyed && event.bullet.tank === this) {
            bullets--
        } else if (event is TankStateAppearing.End && event.tank === this) {
            stateAppearingEnd()
        } else if (event is TankStateInvincible.End && event.tank === this) {
            state = TankStateNormal(imageManager, this)
        } else if (event is TankStateFrozen.End && event.tank === this) {
            state = TankStateNormal(imageManager, this)
        }
    }

    abstract fun stateAppearingEnd()

    fun hit(bullet: BulletHandle) {
        if (isDestroyed) {
            return
        }
        eventManager.fireEvent(Hit(this))
        hitHook(bullet)
    }

    abstract fun hitHook(bullet: BulletHandle)

    override fun destroyHook() {
        eventManager.fireEvent(Destroyed(this))
    }

    override fun dispose() {
        cooldownTimer.dispose()

        state.dispose()

        eventManager.removeSubscriber(this, subscriptions)

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
    }
}