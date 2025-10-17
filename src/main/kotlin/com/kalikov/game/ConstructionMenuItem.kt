package com.kalikov.game

import com.kalikov.engine.MenuItem

class ConstructionMenuItem(
    private val game: BattleCityGame,
    private val sceneProvider: SceneProvider,
) : MenuItem("CONSTRUCTION") {
    override fun execute() {
        game.sceneManager.setNextScene(sceneProvider.constructionScene)
    }
}