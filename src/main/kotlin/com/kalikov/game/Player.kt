package com.kalikov.game

class Player(
    private val eventManager: EventManager,
    private val bonusLifeScore: Int = 20000,
    initialScore: Int = 0
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            PointsFactory.PointsCreated::class,
            Tank.PlayerDestroyed::class,
            PowerUpHandler.Life::class
        )
    }

    var previousScore: Int = 0
        private set

    var lives = 2
        private set
    var score = initialScore
        private set

    var upgradeLevel = 0

    init {
        LeaksDetector.add(this)

        eventManager.addSubscriber(this, subscriptions)
    }

    data object OutOfLives : Event()

    override fun notify(event: Event) {
        when (event) {
            is PointsFactory.PointsCreated -> {
                val previousValue = score
                score += event.points.value
                if (bonusLifeScore in (previousValue + 1)..score) {
                    incrementLife()
                }
            }

            is Tank.PlayerDestroyed -> {
                if (lives == 0) {
                    eventManager.fireEvent(OutOfLives)
                } else {
                    lives--
                }
            }

            is PowerUpHandler.Life -> {
                incrementLife()
            }

            else -> {
            }
        }
    }

    fun reset() {
        previousScore = score
        lives = 2
        score = 0
        upgradeLevel = 0
    }

    private fun incrementLife() {
        lives++
    }

    fun dispose() {
        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}