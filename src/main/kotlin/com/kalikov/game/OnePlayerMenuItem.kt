package com.kalikov.game

import java.time.Clock

class OnePlayerMenuItem(
    private val game: Game,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
) : MainMenuItem("1 PLAYER") {
    override fun execute() {
        stageManager.setPlayersCount(1)
        game.eventManager.fireEvent(
            Scene.Start {
                StageScene(game, stageManager, entityFactory)
            }
        )
    }
}
