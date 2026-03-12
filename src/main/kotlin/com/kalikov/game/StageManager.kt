package com.kalikov.game

import com.kalikov.engine.ScreenSurface

interface StageManager {
    val players: List<Player>

    val stageNumber: Int

    val stageMap: StageMapConfig
    val stageEnemySpawnDelay: Int
    val stageEnemies: List<EnemyGroupConfig>

//    val demoStage: Stage

    var constructionMap: StageMapConfig

    var curtainBackground: ScreenSurface?

    val highScore: Int
    var isGameOver: Boolean

    var isDemo: Boolean

    fun init(stages: List<Stage>, demoStage: Stage, defaultConstructionMap: StageMapConfig)

    fun setPlayersCount(playersCount: Int)

    fun reset()
    fun resetConstruction()

    fun next(loop: Boolean = true)

    fun prev(loop: Boolean = true)

    fun dispose()
}