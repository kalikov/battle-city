package com.kalikov.engine

open class BasicSceneManager : SceneManager {
    private var currentScene: Scene? = null
    private var nextScene: Scene? = null

    override val scene: Scene? get() = currentScene

    init {
        LeaksDetector.add(this)
    }

    override fun setNextScene(newScene: Scene) {
        nextScene = newScene
    }

    override fun update() {
        nextScene?.let {
            currentScene?.deactivate()
            currentScene = it
            it.activate()
        }
        nextScene = null
        currentScene?.update()
    }

    override fun destroy() {
        nextScene = null

        currentScene?.deactivate()
        currentScene = null

        LeaksDetector.remove(this)
    }
}