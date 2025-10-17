package com.kalikov.game

import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.LeaksDetector

class Player(
    private val game: BattleCityGame,
    private val bonusLifeScore: Int = 20000,
    initialScore: Int = 0,
    val index: Int = 0,
) : EventSubscriber {
    data class Score(val player: Player, val points: Int) : Event()

    private companion object {
        const val DEFAULT_LIVES_COUNT = 3

        private val subscriptions = arrayOf(
            Score::class,
            PlayerTank.PlayerDestroyed::class,
            PowerUpHandler.Life::class
        )
    }

    var previousScore: Int = 0
        private set

    var lives = DEFAULT_LIVES_COUNT
        private set
    var score = initialScore
        private set

    var upgradeLevel = 0
    override val identity: Int
        get() = TODO("Not yet implemented")

    data class OutOfLives(val player: Player) : Event()

    override fun notify(event: Event) {
        when (event) {
            is Score -> {
                if (event.player === this) {
                    val previousValue = score
                    score += event.points
                    if (bonusLifeScore in (previousValue + 1) .. score) {
                        incrementLife()
                    }
                }
            }

            is PlayerTank.PlayerDestroyed -> {
                if (event.tank.player === this) {
                    lives--
                    if (lives == 0) {
                        game.eventManager.fireEvent(OutOfLives(this))
                    }
                }
            }

            is PowerUpHandler.Life -> {
                if (event.player === this) {
                    incrementLife()
                }
            }

            else -> {
            }
        }
    }

    fun reset() {
        previousScore = score
        lives = DEFAULT_LIVES_COUNT
        score = 0
        upgradeLevel = 0
    }

    private fun incrementLife() {
        lives++
        game.soundManager.incrementLife.play()
    }

    fun activate() {
        game.eventManager.addSubscriber(this, subscriptions)
    }

    fun deactivate() {
        game.eventManager.removeSubscriber(this, subscriptions)
    }
}