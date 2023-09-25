package com.kalikov.game

class PowerUpHandler(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
) : EventSubscriber {
    data object Life : Event()
    data object Freeze : Event()
    data object ShovelStart : Event()

    private companion object {
        const val HELMET_DURATION = 9000

        private val subscriptions = setOf(
            PowerUp.Pick::class,
            EnemyFactory.EnemyCreated::class,
            Tank.Destroyed::class,
        )
    }

    private val enemies = HashSet<Tank>()

    init {
        eventManager.addSubscriber(this, subscriptions)
    }

    override fun notify(event: Event) {
        if (event is PowerUp.Pick) {
            handle(event.powerUp, event.tank)
        } else if (event is EnemyFactory.EnemyCreated) {
            enemies.add(event.enemy)
        } else if (event is Tank.Destroyed && event.tank.isEnemy()) {
            enemies.remove(event.tank)
        }
    }

    private fun handle(powerUp: PowerUp, playerTank: Tank) {
        eventManager.fireEvent(SoundManager.Play("powerup_pick"))

        when (powerUp.type) {
            PowerUp.Type.GRENADE -> handleGrenade()
            PowerUp.Type.HELMET -> handleHelmet(playerTank)
            PowerUp.Type.TIMER -> handleTimer()
            PowerUp.Type.SHOVEL -> handleShovel()
            PowerUp.Type.STAR -> handleStar(playerTank)
            PowerUp.Type.TANK -> handleTank()
        }
    }

    private fun handleGrenade() {
        enemies.forEach { tank ->
            tank.value = 0
            tank.destroy()
        }
    }

    private fun handleHelmet(playerTank: Tank) {
        val state = TankStateInvincible(eventManager, imageManager, playerTank, HELMET_DURATION)
        playerTank.state = state
    }

    private fun handleTimer() {
        eventManager.fireEvent(Freeze)
    }

    private fun handleShovel() {
        eventManager.fireEvent(ShovelStart)
    }

    private fun handleStar(playerTank: Tank) {
        playerTank.upgrade()
    }

    private fun handleTank() {
        eventManager.fireEvent(Life)
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)
    }
}