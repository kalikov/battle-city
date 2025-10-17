package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.LazyImage
import com.kalikov.engine.Scene
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import com.kalikov.engine.times

class GameOverScene(
    private val game: BattleCityGame,
    private val menuScene: Scene
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
            val highScore = game.stageManager.highScore
            game.stageManager.reset()
            if (highScore < game.stageManager.highScore) {
                game.sceneManager.setNextScene(HighScoreScene(game, menuScene))
            } else {
                game.sceneManager.setNextScene(menuScene)
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

    override fun activate() {
    }

    override fun deactivate() {
        destroy()
    }

    override fun destroy() {
        messageLazyBlender.dispose()
    }
}