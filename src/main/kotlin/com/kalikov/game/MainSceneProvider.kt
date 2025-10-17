package com.kalikov.game

import com.kalikov.engine.BasicSceneManager
import com.kalikov.engine.FontManager
import com.kalikov.engine.Scene

class MainSceneProvider(
    private val game: MainGame,
    private val fontManager: FontManager,
) : BasicSceneManager(), SceneProvider {
    private val scenes = Array<Scene?>(9) { null }
    private var sceneCount = 0

    override val loadingScene: Scene by sceneFactory {
        createLoadingScene()
    }

    override val menuScene: Scene by sceneFactory {
        createMenuScene()
    }
    override val constructionScene: Scene by sceneFactory {
        createConstructionScene()
    }

    override fun destroy() {
        super.destroy()

        while (sceneCount > 0) {
            scenes[sceneCount]?.destroy()
            scenes[sceneCount] = null
            sceneCount--
        }
    }

    private fun <T : Scene> sceneFactory(initializer: () -> T): Lazy<T> {
        return lazy {
            val scene = initializer()
            scenes[sceneCount] = scene
            sceneCount++
            scene
        }
    }

    private fun createLoadingScene(): LoadingScene {
        return LoadingScene(
            game,
            game.imageManager,
            game.soundManager,
            fontManager,
            this,
        )
    }

    private fun createMenuScene(): MenuScene {
        return MenuScene(
            game,
            this,
        )
    }

    private fun createConstructionScene(): ConstructionScene {
        return ConstructionScene(
            game,
            menuScene,
        )
    }
}