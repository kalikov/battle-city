package com.kalikov.game

import com.kalikov.engine.ScreenSurface
import kotlin.math.max
import kotlin.math.min

open class GameStageManager(
    private val game: BattleCityGame
) : StageManager {
    private lateinit var stages: List<Stage>
    private lateinit var defaultConstructionMap: StageMapConfig
    private lateinit var currentConstructionMap: StageMapConfig

    override lateinit var demoStage: Stage

    override lateinit var players: List<Player>

    private var index = 0

    override var highScore: Int = 20000
        protected set

    override var constructionMap: StageMapConfig
        get() = currentConstructionMap
        set(value) {
            currentConstructionMap = value
        }

    override var curtainBackground: ScreenSurface? = null
        set(value) {
            field?.dispose()
            field = value
        }

    override val stageMap: StageMapConfig
        get() {
            if (currentConstructionMap !== defaultConstructionMap) {
                return currentConstructionMap
            }
            return stages[index].map
        }

    override val stageEnemySpawnDelay get() = stages[index].enemySpawnDelay
    override val stageEnemies get() = stages[index].enemies

    override val stageNumber get() = index + 1

    override fun init(stages: List<Stage>, demoStage: Stage, defaultConstructionMap: StageMapConfig) {
        require(stages.isNotEmpty())
        this.stages = stages
        this.demoStage = demoStage

        this.defaultConstructionMap = defaultConstructionMap
        this.currentConstructionMap = defaultConstructionMap
        players = listOf(Player(game))
        players.forEach { it.activate() }
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
        currentConstructionMap = defaultConstructionMap
        highScore = max(highScore, players.maxOf { it.score })
        players.forEach { it.reset() }
    }

    override fun resetConstruction() {
        currentConstructionMap = defaultConstructionMap
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
        players.forEach { it.deactivate() }
        curtainBackground?.dispose()
    }
}