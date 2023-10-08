package com.kalikov.game

import java.time.Clock

class GameOverScene(
    private val screen: Screen,
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
    clock: Clock
) : Scene {
    private val script = Script()

    private val brickBlending = TextureBlending(imageManager.getImage("wall_brick"))

    init {
        script.enqueue(Delay(script, 320, clock))
        script.enqueue(Execute { eventManager.fireEvent(SoundManager.Play("game_over")) })
        script.enqueue(Delay(script, 1800, clock))
        script.enqueue(Execute {
            val highScore = stageManager.highScore
            stageManager.reset()
            if (highScore < stageManager.highScore) {
                eventManager.fireEvent(Scene.Start {
                    HighScoreScene(screen, eventManager, imageManager, stageManager, entityFactory, clock)
                })
            } else {
                eventManager.fireEvent(Scene.Start {
                    MainMenuScene(screen, eventManager, imageManager, stageManager, entityFactory, clock)
                })
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