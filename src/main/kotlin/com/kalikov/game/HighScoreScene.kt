package com.kalikov.game

class HighScoreScene(
    private val game: Game,
    private val stageManager: StageManager,
    private val nextSceneFactory: () -> Scene,
) : Scene {
    companion object {
        const val BLINK_INTERVAL = 32

        const val BLINK_COUNT = 259

        private val BLUE = ARGB.rgb(0x234af7)
        private val GRAY = ARGB.rgb(0x636363)
    }

    private val timer = BasicTimer(game.clock, BLINK_INTERVAL, ::blink)

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

    private val brickBlending = object : TextureBlending(game.imageManager.getImage("wall_brick")) {
        override fun blend(dst: ARGB, src: ARGB, x: Pixel, y: Pixel): ARGB {
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
            game.eventManager.fireEvent(Scene.Start(nextSceneFactory))
        }
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)

        val x = t(2).toPixel() + t(1).toPixel() / 2
        val y = t(6).toPixel() + t(1).toPixel() / 2 + Globals.FONT_BIG_CORRECTION
        val interval = Globals.FONT_BIG_CORRECTION + t(3).toPixel()
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