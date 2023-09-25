package com.kalikov.game

import java.time.Clock

class OnePlayerMenuItem(
    private val screen: Screen,
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
    private val clock: Clock
) : MainMenuItem("1 PLAYER") {
    override fun execute() {
        eventManager.fireEvent(
            Scene.Start {
                StageScene(screen, eventManager, imageManager, stageManager, entityFactory, clock)
            }
        )
    }
}
