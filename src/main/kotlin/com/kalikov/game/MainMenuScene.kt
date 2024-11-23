package com.kalikov.game

class MainMenuScene(
    private val game: Game,
    private val stageManager: StageManager,
) : Scene, EventSubscriber {
    companion object {
        const val MOVE_INTERVAL = 16
        const val DEMO_INTERVAL = 10000

        const val NAMCO_LTD = "1980 1985 NAMCO LTD"

        const val BATTLE = "BATTLE"
        const val CITY = "CITY"

        private val subscriptions = setOf(Keyboard.KeyPressed::class)
    }

    var top = Globals.CANVAS_HEIGHT

    val isActive get() = mainMenuController.isActive

    private val mainMenu: MainMenu
    private var mainMenuController: MainMenuController
    private val mainMenuView: MainMenuView

    private val cursorView: MainMenuCursorView

    private val arriveTimer = BasicTimer(game.clock, MOVE_INTERVAL, ::updatePosition)

    private val demoTimer = BasicTimer(game.clock, DEMO_INTERVAL, ::startDemo)

    private val brickBlending = object : TextureBlending(game.imageManager.getImage("wall_brick")) {
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

    private val mainMenuLazyImage = LazyImage.Custom(game.screen, t(27).toPixel(), t(26).toPixel(), ::drawCache)

    init {
        LeaksDetector.add(this)

        game.eventManager.addSubscriber(this, subscriptions)

        arriveTimer.restart()

        mainMenu = MainMenu(
            OnePlayerMenuItem(game, stageManager),
            TwoPlayersMenuItem(game, stageManager),
            ConstructionMenuItem(game, stageManager)
        )

        mainMenuController = MainMenuController(game.eventManager, mainMenu)
        mainMenuController.isActive = false

        cursorView = MainMenuCursorView(game.imageManager, game.clock)
        mainMenuView = MainMenuView(mainMenu, cursorView)
    }

    private fun updatePosition(count: Int) {
        if (top == px(0)) {
            return
        }
        top -= count
        if (top <= 0) {
            arrived()
        }
    }

    fun arrived() {
        top = px(0)
        cursorView.visible = true
        arriveTimer.stop()
        demoTimer.restart()
    }

    override fun update() {
        arriveTimer.update()
        demoTimer.update()
        cursorView.update()
        if (top == px(0)) {
            mainMenuController.isActive = true
        }
    }

    override fun draw(surface: ScreenSurface) {
        clearCanvas(surface)

        surface.draw(t(2).toPixel(), top + t(2).toPixel(), mainMenuLazyImage.target)

        mainMenuView.draw(surface, top)
    }

    private fun drawCache(surface: ScreenSurface) {
        val nameTop = t(4).toPixel()
        surface.fillText(
            BATTLE,
            t(1).toPixel() + t(1).toPixel() / 2,
            nameTop + Globals.FONT_BIG_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_BIG,
            brickBlending
        )
        surface.fillText(
            CITY,
            t(6).toPixel(),
            nameTop + 2 * Globals.FONT_BIG_CORRECTION + t(3).toPixel() / 2,
            ARGB.WHITE,
            Globals.FONT_BIG,
            brickBlending
        )

        surface.draw(px(2), t(1).toPixel(), game.imageManager.getImage("roman_one"))
        surface.fillRect(t(1).toPixel() + 1, t(1).toPixel() + 3, px(6), px(2), ARGB.WHITE)

        surface.fillText(
            formatScore(stageManager.players[0].previousScore),
            t(1).toPixel() + 1,
            t(1).toPixel() + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )

        if (stageManager.players.size > 1) {
            surface.draw(
                t(19).toPixel() + 2,
                t(1).toPixel(),
                game.imageManager.getImage("roman_two")
            )
            surface.fillRect(t(20).toPixel() + 1, t(1).toPixel() + 3, px(6), px(2), ARGB.WHITE)
            surface.fillText(
                formatScore(stageManager.players[1].previousScore),
                t(20).toPixel() + 1,
                t(1).toPixel() + Globals.FONT_REGULAR_CORRECTION,
                ARGB.WHITE,
                Globals.FONT_REGULAR
            )
        }

        surface.fillText(
            "HI" + formatScore(stageManager.highScore),
            t(9).toPixel() + 1,
            t(1).toPixel() + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )
        surface.fillRect(t(11).toPixel() + 1, t(1).toPixel() + 3, px(6), px(2), ARGB.WHITE)

        surface.draw(t(9).toPixel(), t(21).toPixel(), game.imageManager.getImage("namco"), namcoBlending)

        surface.draw(t(2).toPixel(), t(23).toPixel(), game.imageManager.getImage("copyright"))
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
            arrived()
        }
        if (!demoTimer.isStopped) {
            demoTimer.restart()
        }
    }

    fun setMenuItem(index: Int) {
        mainMenu.item = index
    }

    private fun clearCanvas(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)
    }

    private fun startDemo() {
        demoTimer.stop()
        stageManager.demoStage?.let { demoStage ->
            game.eventManager.fireEvent(Scene.Start {
                DemoStageScene(game, stageManager, mainMenu.item, demoStage)
            })
        }
    }

    override fun destroy() {
        mainMenuLazyImage.dispose()

        mainMenuController.dispose()
        cursorView.dispose()

        game.eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}