package com.kalikov.game

interface StageManager {
    val player: Player

    val stage: Stage
    val stageNumber: Int

    var constructionMap: StageMapConfig

    var curtainBackground: ScreenSurface?

    fun init(stages: List<Stage>, defaultConstructionMap: StageMapConfig)

    fun reset()
    fun resetConstruction()

    fun next(loop: Boolean = true)

    fun prev(loop: Boolean = true)

    fun dispose()
}