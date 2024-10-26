package com.kalikov.game

import kotlin.math.max
import kotlin.math.min

class DefaultStageManager(
    private val game: Game
) : StageManager {
    private lateinit var stages: List<Stage>
    private lateinit var defaultConstructionMap: StageMapConfig
    private lateinit var currentConstructionMap: StageMapConfig
    private lateinit var constructionStage: Stage
    override var demoStage: Stage? = null
        private set

    override lateinit var players: List<Player>

    private var index = 0

    override var highScore: Int = 20000
        private set

    override var constructionMap: StageMapConfig
        get() = currentConstructionMap
        set(value) {
            currentConstructionMap = value
            val stage = stages[index]
            constructionStage = Stage(value, stage.enemySpawnDelay, stage.enemies)
        }

    override var curtainBackground: ScreenSurface? = null

    override val stage: Stage
        get() {
            if (constructionMap !== defaultConstructionMap) {
                return constructionStage
            }
            return stages[index]
        }

    override val stageNumber get() = index + 1

    override fun init(stages: List<Stage>, defaultConstructionMap: StageMapConfig, demoStage: Stage?) {
        require(stages.isNotEmpty())
        this.stages = stages

        this.defaultConstructionMap = defaultConstructionMap
        players = listOf(Player(game))
        constructionStage = Stage(defaultConstructionMap, stages[0].enemySpawnDelay, stages[0].enemies)
        constructionMap = defaultConstructionMap

        this.demoStage = demoStage
    }

    override fun setPlayersCount(playersCount: Int) {
        if (playersCount == players.size) {
            return
        }
        players = List(playersCount) { i ->
            if (i < players.size) players[i] else Player(game, index = i)
        }
    }

    override fun reset() {
        index = 0
        constructionMap = defaultConstructionMap
        constructionStage = Stage(defaultConstructionMap, stages[0].enemySpawnDelay, stages[0].enemies)
        highScore = max(highScore, players.maxOf { it.score })
        players.forEach { it.reset() }
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
        players.forEach { it.dispose() }
    }
}