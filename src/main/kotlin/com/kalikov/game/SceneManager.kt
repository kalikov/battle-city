package com.kalikov.game

class SceneManager(
    private val eventManager: EventManager
) : EventSubscriber {
    var scene: Scene? = null
        private set

    private var nextScene: Scene? = null

    init {
        LeaksDetector.add(this)

        eventManager.addSubscriber(this, setOf(Scene.Start::class))
    }

    fun <T : Scene> setNextScene(sceneFactory: () -> T): T {
        nextScene?.destroy()

        val newScene = sceneFactory()
        nextScene = newScene
        return newScene
    }

    fun update() {
        nextScene?.let {
            scene?.destroy()
            scene = it
        }
        nextScene = null
        scene?.update()
    }

    fun draw(surface: ScreenSurface) {
        scene?.draw(surface)
    }

    fun destroy() {
        nextScene?.destroy()
        nextScene = null

        scene?.destroy()
        scene = null

        eventManager.removeSubscriber(this, setOf(Scene.Start::class))

        LeaksDetector.remove(this)
    }

    override fun notify(event: Event) {
        if (event is Scene.Start) {
            setNextScene(event.sceneFactory)
        }
    }
}