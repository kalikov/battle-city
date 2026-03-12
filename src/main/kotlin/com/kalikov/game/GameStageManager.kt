package com.kalikov.game

import com.kalikov.engine.ScreenSurface
import kotlin.math.max
import kotlin.math.min

class GameStageManager(
    private val game: BattleCityGame
) : StageManager {
    private lateinit var stages: List<Stage>
    private lateinit var defaultConstructionMap: StageMapConfig
    private lateinit var currentConstructionMap: StageMapConfig

    private lateinit var demoStage: Stage
    override var isDemo = false

    override val players = mutableListOf(Player(game))

    private var index = 0

    override var highScore: Int = 20000
        private set

    override var isGameOver: Boolean = false

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
            if (isDemo) {
                return demoStage.map
            }
            if (currentConstructionMap !== defaultConstructionMap) {
                return currentConstructionMap
            }
            return stages[index].map
        }

    override val stageEnemySpawnDelay get() = if (isDemo) demoStage.enemySpawnDelay else stages[index].enemySpawnDelay
    override val stageEnemies get() = if (isDemo) demoStage.enemies else stages[index].enemies

    override val stageNumber get() = index + 1

    override fun init(stages: List<Stage>, demoStage: Stage, defaultConstructionMap: StageMapConfig) {
        require(stages.isNotEmpty())
        this.stages = stages
        this.demoStage = demoStage

        this.defaultConstructionMap = defaultConstructionMap
        this.currentConstructionMap = defaultConstructionMap
    }

    override fun setPlayersCount(playersCount: Int) {
        if (playersCount == players.size) {
            return
        }
        while (playersCount < players.size) {
            players.removeLast()
        }
        while (playersCount > players.size) {
            val player = Player(game, index = players.size)
            players.add(player)
        }
    }

    override fun reset() {
        index = 0
        currentConstructionMap = defaultConstructionMap
        highScore = max(highScore, players.maxOf { it.score })
        players.forEach { it.reset() }
        isGameOver = false
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
        curtainBackground?.dispose()
    }
}