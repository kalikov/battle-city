package com.kalikov.engine

interface SceneManager {
    val scene: Scene?

    fun setNextScene(newScene: Scene)

    fun update()

    fun destroy()
}