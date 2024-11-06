package com.kalikov.game

class GameOverScene(
    private val game: Game,
    private val stageManager: StageManager,
) : Scene {
    private val script = Script()

    private val brickBlending = TextureBlending(game.imageManager.getImage("wall_brick"))

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
        val y = t(9).toPixel() + Globals.FONT_BIG_CORRECTION
        val interval = Globals.FONT_BIG_CORRECTION + t(3).toPixel()
        surface.fillText("GAME", x, y, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
        surface.fillText("OVER", x, y + interval, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
    }

    override fun destroy() {
    }
}