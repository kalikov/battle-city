package com.kalikov.game

import com.kalikov.engine.Scene

interface SceneProvider {
    val loadingScene: Scene

    val menuScene: Scene

    val constructionScene: Scene

    fun destroy()
}