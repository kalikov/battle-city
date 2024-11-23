package com.kalikov.game

class GameOverScene(
    private val game: Game,
    private val stageManager: StageManager,
) : Scene {
    private companion object {
        const val GAME = "GAME"
        const val OVER = "OVER"
    }

    private val script = Script()

    private val brickBlending = TextureBlending(game.imageManager.getImage("wall_brick"))

    private val messageLazyBlender = LazyImage.Custom(
        game.screen,
        GAME.length * Globals.FONT_BIG_SIZE,
        2 * Globals.FONT_BIG_CORRECTION + t(3).toPixel()
    ) {
        val interval = t(3).toPixel()
        it.fillText(GAME, px(0), Globals.FONT_BIG_CORRECTION, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
        it.fillText(OVER, px(0), 2 * Globals.FONT_BIG_CORRECTION + interval, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
    }

    init {
        script.enqueue(Delay(script, 320, game.clock))
        script.enqueue(Execute { game.soundManager.gameOver.play() })
        script.enqueue(Delay(script, 1800, game.clock))
        script.enqueue(Execute {
            val highScore = stageManager.highScore
            stageManager.reset()
            val mainMenuFactory = {
                val mainMenu = MainMenuScene(game, stageManager)
                if (stageManager.players.size == 2) {
                    mainMenu.setMenuItem(1)
                }
                mainMenu
            }
            if (highScore < stageManager.highScore) {
                game.eventManager.fireEvent(Scene.Start {
                    HighScoreScene(game, stageManager, mainMenuFactory)
                })
            } else {
                game.eventManager.fireEvent(Scene.Start(mainMenuFactory))
            }
        })
    }

    override fun update() {
        script.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)

        val x = t(8).toPixel()
        val y = t(9).toPixel()

        surface.draw(x, y, messageLazyBlender.target)
    }

    override fun destroy() {
        messageLazyBlender.dispose()
    }
}