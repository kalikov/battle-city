package com.kalikov.game

import com.kalikov.engine.Scene

interface SceneProvider {
    val loadingScene: Scene

    val menuScene: Scene
    val resetMenuScene: Scene

    val constructionScene: Scene

    val demoStageScene: Scene

    val stageScene: Scene

    val stageScoreScene: Scene

    val highScoreScene: Scene

    val gameOverScene: Scene

    fun destroy()
}