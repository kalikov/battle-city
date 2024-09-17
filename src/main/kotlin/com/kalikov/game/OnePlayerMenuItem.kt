package com.kalikov.game

class OnePlayerMenuItem(
    private val game: Game,
    private val stageManager: StageManager,
) : MainMenuItem("1 PLAYER") {
    override fun execute() {
        stageManager.setPlayersCount(1)
        game.eventManager.fireEvent(
            Scene.Start {
                StageScene(game, stageManager)
            }
        )
    }
}
