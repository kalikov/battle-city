package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.BasicTimer
import com.kalikov.engine.Blending
import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.Keyboard
import com.kalikov.engine.LazyImage
import com.kalikov.engine.LeaksDetector
import com.kalikov.engine.Menu
import com.kalikov.engine.Pixel
import com.kalikov.engine.Scene
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import com.kalikov.engine.times
import kotlin.reflect.KClass

class MenuScene(
    private val game: BattleCityGame,
    private val sceneProvider: SceneProvider,
    menu: Menu? = null,
) : Scene, EventSubscriber {
    internal companion object {
        const val MOVE_INTERVAL = 16
        const val DEMO_INTERVAL = 10000

        private const val NAMCO_LTD = "1980 1985 NAMCO LTD"

        private const val BATTLE = "BATTLE"
        private const val CITY = "CITY"

        private val subscriptions = arrayOf<KClass<out Event>>(Keyboard.KeyPressed::class)
    }

    override val identity get() = Globals.IDENTITY_MENU_SCENE

    var top = Globals.CANVAS_HEIGHT

    var isActive = false

    val resetMenuScene: Scene = object : Scene by this {
        override fun activate() {
            reset()
            this@MenuScene.activate()
        }
    }

    private val menu = menu ?: Menu(
        OnePlayerMenuItem(game, sceneProvider),
        TwoPlayersMenuItem(game, sceneProvider),
        ConstructionMenuItem(game, sceneProvider),
    )
    private val menuView: MenuView

    private val cursorView: MenuCursorView

    private val arriveTimer = BasicTimer(game.clock, MOVE_INTERVAL, ::updatePosition)

    private val demoTimer = BasicTimer(game.clock, DEMO_INTERVAL, ::startDemo)

    private val brickBlending = object : TextureBlending(game.imageManager.brickWall) {
        override fun blend(dst: ARGB, src: ARGB, x: Pixel, y: Pixel): ARGB {
            val pixel = super.blend(dst, src, x, y)
            if (pixel == ARGB.rgb(0x636363)) {
                return ARGB.WHITE
            }
            return ARGB.rgb(0xB53121)
        }
    }

    private val namcoBlending = Blending { dst, src, _, _ ->
        ARGB.rgb(0xB53121).and(src).over(dst)
    }

    private val mainMenuLazyImage = LazyImage.Custom(game.screen, 27.tiles.toPixel(), 26.tiles.toPixel(), ::drawCache)

    init {
        LeaksDetector.add(this)

        cursorView = MenuCursorView(game.imageManager, game.clock)
        menuView = MenuView(this.menu, cursorView)
    }

    private fun updatePosition(count: Int) {
        if (top == 0.px) {
            return
        }
        top -= count
        if (top <= 0) {
            arriveAndRestartDemoTimer()
        }
    }

    fun arrive() {
        top = 0.px
        cursorView.visible = true
        arriveTimer.stop()
    }

    private fun arriveAndRestartDemoTimer() {
        arrive()
        demoTimer.restart()
    }

    override fun update() {
        arriveTimer.update()
        demoTimer.update()
        cursorView.update()
        if (top == 0.px) {
            isActive = true
        }
    }

    override fun draw(surface: ScreenSurface) {
        clearCanvas(surface)

        surface.draw(2.tiles.toPixel(), top + 2.tiles.toPixel(), mainMenuLazyImage.target, Blending.Src)

        menuView.draw(surface, top)
    }

    private fun drawCache(surface: ScreenSurface) {
        clearCanvas(surface)

        val nameTop = 4.tiles.toPixel()
        surface.fillText(
            BATTLE,
            1.tiles.toPixel() + 1.tiles.toPixel() / 2,
            nameTop + Globals.FONT_BIG_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_BIG,
            brickBlending
        )
        surface.fillText(
            CITY,
            6.tiles.toPixel(),
            nameTop + 2 * Globals.FONT_BIG_CORRECTION + t(3).toPixel() / 2,
            ARGB.WHITE,
            Globals.FONT_BIG,
            brickBlending
        )

        surface.draw(2.px, t(1).toPixel(), game.imageManager.romanOne)
        surface.fillRect(t(1).toPixel() + 1, t(1).toPixel() + 3, px(6), px(2), ARGB.WHITE)

        surface.fillText(
            formatScore(game.stageManager.players[0].previousScore),
            1.tiles.toPixel() + 1,
            1.tiles.toPixel() + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )

        if (game.stageManager.players.size > 1) {
            surface.draw(
                19.tiles.toPixel() + 2,
                1.tiles.toPixel(),
                game.imageManager.romanTwo
            )
            surface.fillRect(20.tiles.toPixel() + 1, 1.tiles.toPixel() + 3, 6.px, 2.px, ARGB.WHITE)
            surface.fillText(
                formatScore(game.stageManager.players[1].previousScore),
                20.tiles.toPixel() + 1,
                1.tiles.toPixel() + Globals.FONT_REGULAR_CORRECTION,
                ARGB.WHITE,
                Globals.FONT_REGULAR
            )
        }

        surface.fillText(
            "HI" + formatScore(game.stageManager.highScore),
            9.tiles.toPixel() + 1,
            1.tiles.toPixel() + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )
        surface.fillRect(t(11).toPixel() + 1, t(1).toPixel() + 3, px(6), px(2), ARGB.WHITE)

        surface.draw(t(9).toPixel(), t(21).toPixel(), game.imageManager.namco, namcoBlending)

        surface.draw(t(2).toPixel(), t(23).toPixel(), game.imageManager.copyright)
        surface.fillText(
            NAMCO_LTD,
            t(4).toPixel() + 1,
            t(23).toPixel() + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )
        surface.fillText(
            ".",
            t(4).toPixel() + 1 + NAMCO_LTD.length * Globals.FONT_REGULAR_SIZE - 1,
            t(23).toPixel() + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )
        surface.fillText(
            "ALL RIGHTS RESERVED",
            t(4).toPixel() + 1,
            t(25).toPixel() + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )
    }

    private fun formatScore(score: Int): String {
        return "${score / 10}".padStart(6, ' ') + "0"
    }

    override fun notify(event: Event) {
        if (event is Keyboard.KeyPressed && event.playerIndex == 0) {
            keyPressed(event.key)
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (key == Keyboard.Key.START || key == Keyboard.Key.SELECT) {
            arriveAndRestartDemoTimer()
        }
        if (!demoTimer.isStopped) {
            demoTimer.restart()
        }
        if (isActive) {
            if (key == Keyboard.Key.SELECT) {
                menu.nextItem()
            } else if (key == Keyboard.Key.START) {
                menu.executeCurrentItem()
            }
        }
    }

    private fun clearCanvas(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)
    }

    private fun startDemo() {
        game.sceneManager.setNextScene(sceneProvider.demoStageScene)
    }

    override fun activate() {
        game.eventManager.addSubscriber(this, subscriptions)

        mainMenuLazyImage.redraw()
        if (top > 0) {
            if (arriveTimer.isStopped) {
                arriveTimer.restart()
            }
        } else {
            demoTimer.restart()
        }
    }

    override fun deactivate() {
        arrive()
        demoTimer.stop()

        game.eventManager.removeSubscriber(this, subscriptions)
    }

    private fun reset() {
        top = Globals.CANVAS_HEIGHT

        arriveTimer.stop()
        demoTimer.stop()

        isActive = false
        cursorView.visible = false
    }

    override fun destroy() {
        mainMenuLazyImage.dispose()

        LeaksDetector.remove(this)
    }
}