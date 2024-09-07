package com.kalikov.game

import java.time.Clock

class ConstructionMenuItem(
    private val game: Game,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
    private val clock: Clock
) : MainMenuItem("CONSTRUCTION") {
    override fun execute() {
        game.eventManager.fireEvent(Scene.Start {
            ConstructionScene(
                game,
                stageManager,
                entityFactory,
                clock
            )
        })
    }
}