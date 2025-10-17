package com.kalikov.game

import com.kalikov.engine.MenuItem
import com.kalikov.engine.Scene

class TwoPlayersMenuItem(
    private val game: BattleCityGame,
    private val menuScene: Scene,
) : MenuItem("2 PLAYERS") {
    override fun execute() {
        game.stageManager.setPlayersCount(2)
        game.sceneManager.setNextScene(
            StageScene(game, menuScene)
        )
    }
}
