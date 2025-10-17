package com.kalikov.game

import com.kalikov.engine.MenuItem
import com.kalikov.engine.Scene

class OnePlayerMenuItem(
    private val game: BattleCityGame,
    private val menuScene: Scene,
) : MenuItem("1 PLAYER") {
    override fun execute() {
        game.stageManager.setPlayersCount(1)
        game.sceneManager.setNextScene(
            StageScene(game, menuScene)
        )
    }
}
