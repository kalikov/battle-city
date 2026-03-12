package com.kalikov.game

import com.kalikov.engine.Event

class Player(
    private val game: BattleCityGame,
    private val bonusLifeScore: Int = 20000,
    initialScore: Int = 0,
    val index: Int = 0,
) {
    private companion object {
        const val DEFAULT_LIVES_COUNT = 3
    }

    var previousScore: Int = 0
        private set
    var lives = DEFAULT_LIVES_COUNT
        private set

    var score = initialScore
        private set
    var upgradeLevel = 0

    var stageScore = StageScore()
        private set

    data class OutOfLives(val player: Player) : Event()

    val outOfLivesEvent = OutOfLives(this)

    fun score(points: Int) {
        val previousValue = score
        score += points
        if (bonusLifeScore in (previousValue + 1) .. score) {
            incrementLife()
        }
    }

    fun die() {
        lives--
        if (lives == 0) {
            game.eventManager.fireEvent(outOfLivesEvent)
        }
    }

    fun reset() {
        previousScore = score
        lives = DEFAULT_LIVES_COUNT
        score = 0
        upgradeLevel = 0
        stageScore.reset()
    }

    fun incrementLife() {
        lives++
        game.soundManager.incrementLife.play()
    }
}