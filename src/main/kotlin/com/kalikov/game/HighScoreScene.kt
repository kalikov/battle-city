package com.kalikov.game

import java.time.Clock

class HighScoreScene(
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val stageManager: StageManager,
    private val nextSceneFactory: () -> Scene,
    clock: Clock,
) : Scene {
    companion object {
        const val BLINK_INTERVAL = 32

        const val BLINK_COUNT = 259

        private val BLUE = ARGB.rgb(0x234af7)
        private val GRAY = ARGB.rgb(0x636363)
    }

    private val timer = BasicTimer(clock, BLINK_INTERVAL, ::blink)

    private var counter = 0
    // 8 - gray
    // 40 - gray
    // 41 - blue
    // 43 - black
    // 70 - gray
    // 71 - blue
    // 101 - blue
    // 236 - white
    // 237 - blue
    // 250 - white
    // 251 - gray
    // 252 - white
    // 253 - blue
    // 259 - stop

    private val brickBlending = object : TextureBlending(imageManager.getImage("wall_brick")) {
        override fun blend(dst: ARGB, src: ARGB, x: Int, y: Int): ARGB {
            val pixel = super.blend(dst, src, x, y)
            if (pixel == ARGB.rgb(0x9c4A00)) {
                if (counter % 2 == 0) {
                    if (counter < 8 || counter == 14) {
                        return BLUE
                    }
                    if (counter >= 236) {
                        return ARGB.WHITE
                    }
                    return GRAY
                }
                if (counter < 11) {
                    return ARGB.WHITE
                }
                if (counter == 41 || counter == 71 || counter == 101 || counter == 131 || counter == 161 || counter == 191 || counter == 211 || counter == 221 || counter in 237..249 || counter >= 253) {
                    return BLUE
                }
                if (counter == 251) {
                    return GRAY
                }
                return ARGB.BLACK
            }
            return pixel
        }
    }

    override fun update() {
        if (timer.isStopped) {
            timer.restart()
        }
        timer.update()
    }

    private fun blink() {
        counter++
        if (counter == BLINK_COUNT) {
            timer.stop()
            eventManager.fireEvent(Scene.Start(nextSceneFactory))
        }
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)

        val x = 2 * Globals.TILE_SIZE + Globals.TILE_SIZE / 2
        val y = 6 * Globals.TILE_SIZE + Globals.TILE_SIZE / 2 + Globals.FONT_BIG_CORRECTION
        val interval = Globals.FONT_BIG_SIZE + 2 * Globals.TILE_SIZE + Globals.TILE_SIZE / 2
        surface.fillText("HISCORE", x, y, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
        surface.fillText(
            "${stageManager.highScore}".padStart(7, ' '),
            x,
            y + interval,
            ARGB.WHITE,
            Globals.FONT_BIG,
            brickBlending
        )
    }

    override fun destroy() {
        timer.dispose()
    }
}