package com.kalikov.game

import java.time.Clock

class TwoPlayersMenuItem(
    private val screen: Screen,
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
    private val clock: Clock
) : MainMenuItem("2 PLAYERS") {
    override fun execute() {
        stageManager.setPlayersCount(2)
        eventManager.fireEvent(
            Scene.Start {
                StageScene(screen, eventManager, imageManager, stageManager, entityFactory, clock)
            }
        )
    }
}
