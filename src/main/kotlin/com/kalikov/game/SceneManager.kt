package com.kalikov.game

class SceneManager(
    private val eventManager: EventManager
) : EventSubscriber {
    var scene: Scene? = null
        private set

    init {
        LeaksDetector.add(this)

        eventManager.addSubscriber(this, setOf(Scene.Start::class))
    }

    fun <T : Scene> setScene(sceneFactory: () -> T): T {
        scene?.destroy()

        val newScene = sceneFactory()
        scene = newScene
        return newScene
    }

    fun update() {
        scene?.update()
    }

    fun draw(surface: ScreenSurface) {
        scene?.draw(surface)
    }

    fun destroy() {
        scene?.destroy()
        scene = null

        eventManager.removeSubscriber(this, setOf(Scene.Start::class))
        eventManager.destroy()

        LeaksDetector.remove(this)
    }

    override fun notify(event: Event) {
        if (event is Scene.Start) {
            setScene(event.sceneFactory)
        }
    }
}