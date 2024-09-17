package com.kalikov.game

class ConstructionMenuItem(
    private val game: Game,
    private val stageManager: StageManager,
) : MainMenuItem("CONSTRUCTION") {
    override fun execute() {
        game.eventManager.fireEvent(Scene.Start {
            ConstructionScene(
                game,
                stageManager,
            )
        })
    }
}