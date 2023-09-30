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
            eventManager.fireEvent(Scene.Start {
                MainMenuScene(screen, eventManager, imageManager, stageManager, entityFactory, clock)
            })
        })
    }

    override fun update() {
        script.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)

        surface.fillText("GAME", 64, 64 + 32 - 4, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
        surface.fillText("OVER", 64, 64 + 64 + 16, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
    }

    override fun destroy() {
    }
}