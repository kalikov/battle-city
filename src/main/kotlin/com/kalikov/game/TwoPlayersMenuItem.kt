package com.kalikov.game

import com.kalikov.engine.MenuItem

class TwoPlayersMenuItem(
    private val game: BattleCityGame,
    private val sceneProvider: SceneProvider,
) : MenuItem("2 PLAYERS") {
    override fun execute() {
        game.stageManager.setPlayersCount(2)
        game.sceneManager.setNextScene(sceneProvider.stageScene)
    }
}
