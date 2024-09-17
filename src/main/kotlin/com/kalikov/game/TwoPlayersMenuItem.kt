package com.kalikov.game

class TwoPlayersMenuItem(
    private val game: Game,
    private val stageManager: StageManager,
) : MainMenuItem("2 PLAYERS") {
    override fun execute() {
        stageManager.setPlayersCount(2)
        game.eventManager.fireEvent(
            Scene.Start {
                StageScene(game, stageManager)
            }
        )
    }
}
