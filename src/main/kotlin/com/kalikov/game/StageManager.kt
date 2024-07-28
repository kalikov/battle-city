package com.kalikov.game

interface StageManager {
    val players: List<Player>

    val stage: Stage
    val stageNumber: Int

    var constructionMap: StageMapConfig

    var curtainBackground: ScreenSurface?

    val highScore: Int

    fun init(stages: List<Stage>, defaultConstructionMap: StageMapConfig)

    fun setPlayersCount(playersCount: Int)

    fun reset()
    fun resetConstruction()

    fun next(loop: Boolean = true)

    fun prev(loop: Boolean = true)

    fun dispose()
}