package com.kalikov.game

import kotlin.math.max
import kotlin.math.min

class DefaultStageManager(
    private val eventManager: EventManager
) : StageManager {
    private lateinit var stages: List<Stage>
    private lateinit var defaultConstructionMap: StageMapConfig
    private lateinit var currentConstructionMap: StageMapConfig
    private lateinit var constructionStage: Stage

    override lateinit var player: Player

    private var index = 0

    override var constructionMap: StageMapConfig
        get() = currentConstructionMap
        set(value) {
            currentConstructionMap = value
            val stage = stages[index].enemies
            constructionStage = Stage(value, stage)
        }

    override var curtainBackground: ScreenSurface? = null

    override fun init(stages: List<Stage>, defaultConstructionMap: StageMapConfig) {
        require(stages.isNotEmpty())
        this.stages = stages
        this.defaultConstructionMap = defaultConstructionMap
        player = Player(eventManager)
        constructionStage = Stage(defaultConstructionMap, stages[0].enemies)
        constructionMap = defaultConstructionMap
    }

    override val stage: Stage
        get() {
            if (constructionMap !== defaultConstructionMap) {
                return constructionStage
            }
            return stages[index]
        }

    override val stageNumber: Int
        get() = index + 1

    override fun reset() {
        index = 0
        constructionMap = defaultConstructionMap
        constructionStage = Stage(defaultConstructionMap, stages[0].enemies)
        player.dispose()
        player = Player(eventManager)
    }

    override fun resetConstruction() {
        constructionMap = defaultConstructionMap
    }

    override fun next(loop: Boolean) {
        index = if (loop) {
            if (index + 1 >= stages.size) 0 else index + 1
        } else {
            min(stages.size - 1, index + 1)
        }
    }

    override fun prev(loop: Boolean) {
        if (loop) {
            if (index > 0) index - 1 else stages.size - 1
        } else {
            index = max(0, index - 1)
        }
    }

    override fun dispose() {
        player.dispose()
    }
}