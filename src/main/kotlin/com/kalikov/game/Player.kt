package com.kalikov.game

class Player(private val eventManager: EventManager) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            PointsFactory.PointsCreated::class,
            Tank.PlayerDestroyed::class,
            PowerUpHandler.Life::class
        )
    }

    var lives = 2
        private set
    var score = 0

    var upgradeLevel = 0

    init {
        LeaksDetector.add(this)

        eventManager.addSubscriber(this, subscriptions)
    }

    data object OutOfLives : Event()

    override fun notify(event: Event) {
        when (event) {
            is PointsFactory.PointsCreated -> {
                score += event.points.value
            }

            is Tank.PlayerDestroyed -> {
                if (lives == 0) {
                    eventManager.fireEvent(OutOfLives)
                } else {
                    lives--
                }
            }

            is PowerUpHandler.Life -> {
                lives++
            }

            else -> {
            }
        }
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}