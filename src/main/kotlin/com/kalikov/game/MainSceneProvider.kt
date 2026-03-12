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

    override val menuScene: MenuScene by sceneFactory {
        createMenuScene()
    }

    override val resetMenuScene get() = menuScene.resetMenuScene

    override val constructionScene: Scene by sceneFactory {
        createConstructionScene()
    }

    override val demoStageScene: Scene by sceneFactory {
        createDemoStageScene()
    }

    override val stageScene: Scene by sceneFactory {
        createStageScene()
    }

    override val stageScoreScene: Scene by sceneFactory {
        createStageScoreScene()
    }

    override val highScoreScene: Scene by sceneFactory {
        createHighScoreScene()
    }

    override val gameOverScene: Scene by sceneFactory {
        createGameOverScene()
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

    private fun createDemoStageScene(): DemoStageScene {
        return DemoStageScene(
            game,
            menuScene,
            resetMenuScene,
        )
    }

    private fun createStageScene(): StageScene {
        return StageScene(
            game,
            this,
        )
    }

    private fun createStageScoreScene(): StageScoreScene {
        return StageScoreScene(
            game,
            this,
        )
    }

    private fun createHighScoreScene(): HighScoreScene {
        return HighScoreScene(
            game,
            resetMenuScene,
        )
    }

    private fun createGameOverScene(): GameOverScene {
        return GameOverScene(
            game,
            this,
        )
    }
}