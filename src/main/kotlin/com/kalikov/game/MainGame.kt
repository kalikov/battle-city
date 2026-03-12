package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Audio
import com.kalikov.engine.ConcurrentImageManager
import com.kalikov.engine.ConcurrentSoundManager
import com.kalikov.engine.EventManager
import com.kalikov.engine.Game
import com.kalikov.engine.Input
import com.kalikov.engine.SceneManager
import com.kalikov.engine.Screen
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import java.awt.Font
import java.time.Clock

class MainGame(
    override val config: GameConfig,
    private val input: Input,
    override val screen: Screen,
    override val eventManager: EventManager,
    override val sceneManager: SceneManager,
    audio: Audio,
) : Game(config.fpsLimit, input, screen, eventManager, sceneManager), BattleCityGame {
    private var duration = 0L

    override val identity get() = Globals.IDENTITY_GAME

    override val clock: Clock get() = Clock.systemUTC()

    override val soundManager = ConcurrentSoundManager(audio)
    override val imageManager = ConcurrentImageManager(screen)
    override val stageManager = GameStageManager(this)

    override fun think(): Boolean {
        val start = System.nanoTime()
        val ret = super.think()
        if (ret) {
            duration = System.nanoTime() - start
        }
        return ret
    }

    override fun onDraw(surface: ScreenSurface) {
        if (config.debug) {
            screen.surface.fillText(
                "${duration / 1000000}.".padStart(4, ' ') + "${duration % 1000000 / 1000}ms".padStart(5, '0'),
                0.px,
                2.tiles.toPixel(),
                ARGB.Companion.WHITE,
                "${Font.MONOSPACED}-${2.tiles.toPixel().toInt()}"
            )

            val lastKeyPressed = input.lastKeyPressed
            if (lastKeyPressed != 0) {
                screen.surface.fillText(
                    "$lastKeyPressed".padStart(3, ' '),
                    screen.surface.width - 6.tiles.toPixel(),
                    2.tiles.toPixel(),
                    ARGB.Companion.WHITE,
                    "${Font.MONOSPACED}-${2.tiles.toPixel().toInt()}"
                )
            }
        }
    }

    override fun onDestroy() {
        stageManager.dispose()
        soundManager.destroy()
        imageManager.destroy()
    }
}