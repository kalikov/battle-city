package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.script.Delay
import com.kalikov.engine.script.Execute
import com.kalikov.engine.LazyImage
import com.kalikov.engine.Scene
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.script.Script
import com.kalikov.engine.px
import com.kalikov.engine.times

class GameOverScene(
    private val game: BattleCityGame,
    private val sceneProvider: SceneProvider,
) : Scene {
    private companion object {
        const val GAME = "GAME"
        const val OVER = "OVER"
    }

    private val script = Script()

    private val brickBlending = TextureBlending(game.imageManager.brickWall)

    private val messageLazyBlender = LazyImage.Custom(
        game.screen,
        GAME.length * Globals.FONT_BIG_SIZE,
        2 * Globals.FONT_BIG_CORRECTION + 3.tiles.toPixel()
    ) {
        val interval = 3.tiles.toPixel()
        it.fillText(GAME, 0.px, Globals.FONT_BIG_CORRECTION, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
        it.fillText(OVER, 0.px, 2 * Globals.FONT_BIG_CORRECTION + interval, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
    }

    override fun update() {
        script.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)

        val x = 8.tiles.toPixel()
        val y = 9.tiles.toPixel()

        surface.draw(x, y, messageLazyBlender.target)
    }

    override fun activate() {
        script.clear()
        script.enqueue(Delay(script, 320, game.clock))
        script.enqueue(Execute { game.soundManager.gameOver.play() })
        script.enqueue(Delay(script, 1800, game.clock))
        script.enqueue(Execute {
            val highScore = game.stageManager.highScore
            game.stageManager.reset()
            if (highScore < game.stageManager.highScore) {
                game.sceneManager.setNextScene(sceneProvider.highScoreScene)
            } else {
                game.sceneManager.setNextScene(sceneProvider.resetMenuScene)
            }
        })
    }

    override fun deactivate() {
    }

    override fun destroy() {
        messageLazyBlender.dispose()
    }
}