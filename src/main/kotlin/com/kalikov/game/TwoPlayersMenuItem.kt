package com.kalikov.game

import java.time.Clock

class TwoPlayersMenuItem(
    private val game: Game,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
    private val clock: Clock
) : MainMenuItem("2 PLAYERS") {
    override fun execute() {
        stageManager.setPlayersCount(2)
        game.eventManager.fireEvent(
            Scene.Start {
                StageScene(game, stageManager, entityFactory, clock)
            }
        )
    }
}
