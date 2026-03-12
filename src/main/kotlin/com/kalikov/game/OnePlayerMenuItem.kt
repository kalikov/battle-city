package com.kalikov.game

import com.kalikov.engine.MenuItem

class OnePlayerMenuItem(
    private val game: BattleCityGame,
    private val sceneProvider: SceneProvider,
) : MenuItem("1 PLAYER") {
    override fun execute() {
        game.stageManager.setPlayersCount(1)
        game.sceneManager.setNextScene(sceneProvider.stageScene)
    }
}
