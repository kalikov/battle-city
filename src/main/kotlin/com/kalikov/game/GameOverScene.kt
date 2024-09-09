package com.kalikov.game

class GameOverScene(
    private val game: Game,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
) : Scene {
    private val script = Script()

    private val brickBlending = TextureBlending(game.imageManager.getImage("wall_brick"))

    init {
        script.enqueue(Delay(script, 320, game.clock))
        script.enqueue(Execute { game.eventManager.fireEvent(SoundManager.Play("game_over")) })
        script.enqueue(Delay(script, 1800, game.clock))
        script.enqueue(Execute {
            val highScore = stageManager.highScore
            stageManager.reset()
            val mainMenuFactory = {
                val mainMenu = MainMenuScene(game, stageManager, entityFactory)
                if (stageManager.players.size == 2) {
                    mainMenu.setMenuItem(1)
                }
                mainMenu
            }
            if (highScore < stageManager.highScore) {
                game.eventManager.fireEvent(Scene.Start {
                    HighScoreScene(game.eventManager, game.imageManager, stageManager, mainMenuFactory, game.clock)
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

        val x = Globals.TILE_SIZE * 8
        val y = Globals.TILE_SIZE * 9 + Globals.FONT_BIG_CORRECTION
        val interval = Globals.FONT_BIG_SIZE + Globals.UNIT_SIZE + Globals.TILE_SIZE / 2
        surface.fillText("GAME", x, y, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
        surface.fillText("OVER", x, y + interval, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
    }

    override fun destroy() {
    }
}