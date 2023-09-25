package com.kalikov.game

import java.time.Clock

class ConstructionMenuItem(
    private val screen: Screen,
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
    private val clock: Clock
) : MainMenuItem("CONSTRUCTION") {
    override fun execute() {
        eventManager.fireEvent(Scene.Start {
            ConstructionScene(
                screen,
                eventManager,
                imageManager,
                stageManager,
                entityFactory,
                clock
            )
        })
    }
}