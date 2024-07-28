package com.kalikov.game

import java.time.Clock

class MainMenuScene(
    screen: Screen,
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val stageManager: StageManager,
    entityFactory: EntityFactory,
    clock: Clock
) : Scene, EventSubscriber {
    companion object {
        const val INTERVAL = 16

        const val NAMCO_LTD = "1980 1985 NAMCO LTD"

        private val subscriptions = setOf(Keyboard.KeyPressed::class)
    }

    var top: Int = Globals.CANVAS_HEIGHT

    val isActive get() = mainMenuController.isActive

    private val mainMenu: MainMenu
    private var mainMenuController: MainMenuController
    private val mainMenuView: MainMenuView

    private val cursorView: MainMenuCursorView

    private val arriveTimer = BasicTimer(clock, INTERVAL, this::updatePosition)

    private val brickBlending = object : TextureBlending(imageManager.getImage("wall_brick")) {
        override fun blend(dst: ARGB, src: ARGB, x: Int, y: Int): ARGB {
            val pixel = super.blend(dst, src, x, y - top)
            if (pixel == ARGB.rgb(0x636363)) {
                return ARGB.WHITE
            }
            return ARGB.rgb(0xB53121)
        }
    }

    private val namco = imageManager.getImage("namco")

    init {
        LeaksDetector.add(this)

        eventManager.addSubscriber(this, subscriptions)

        arriveTimer.restart()

        mainMenu = MainMenu(
            OnePlayerMenuItem(screen, eventManager, imageManager, stageManager, entityFactory, clock),
            TwoPlayersMenuItem(screen, eventManager, imageManager, stageManager, entityFactory, clock),
            ConstructionMenuItem(screen, eventManager, imageManager, stageManager, entityFactory, clock)
        )

        mainMenuController = MainMenuController(eventManager, mainMenu)
        mainMenuController.isActive = false

        cursorView = MainMenuCursorView(imageManager, clock)
        mainMenuView = MainMenuView(mainMenu, cursorView)
    }

    private fun updatePosition(count: Int) {
        if (top == 0) {
            return
        }
        top -= count
        if (top <= 0) {
            arrived()
        }
    }

    fun arrived() {
        top = 0
        cursorView.visible = true
        arriveTimer.stop()
    }

    override fun update() {
        arriveTimer.update()
        cursorView.update()
        if (top == 0) {
            mainMenuController.isActive = true
        }
    }

    override fun draw(surface: ScreenSurface) {
        clearCanvas(surface)

        val nameTop = top + 6 * Globals.TILE_SIZE + Globals.FONT_BIG_CORRECTION
        surface.fillText(
            "BATTLE",
            3 * Globals.TILE_SIZE + Globals.TILE_SIZE / 2,
            nameTop,
            ARGB.WHITE,
            Globals.FONT_BIG,
            brickBlending
        )
        surface.fillText(
            "CITY",
            8 * Globals.TILE_SIZE,
            nameTop + Globals.FONT_BIG_SIZE + Globals.TILE_SIZE,
            ARGB.WHITE,
            Globals.FONT_BIG,
            brickBlending
        )

        surface.draw(2 * Globals.TILE_SIZE + 2, top + 3 * Globals.TILE_SIZE, imageManager.getImage("roman_one"))
        surface.fillRect(3 * Globals.TILE_SIZE + 1, top + 3 * Globals.TILE_SIZE + 3, 6, 2, ARGB.WHITE)
        surface.fillText(
            "${stageManager.players[0].previousScore / 10}".padStart(6, ' ') + "0",
            3 * Globals.TILE_SIZE + 1,
            top + 3 * Globals.TILE_SIZE + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )

        if (stageManager.players.size > 1) {
            surface.draw(21 * Globals.TILE_SIZE + 2, top + 3 * Globals.TILE_SIZE, imageManager.getImage("roman_two"))
            surface.fillRect(22 * Globals.TILE_SIZE + 1, top + 3 * Globals.TILE_SIZE + 3, 6, 2, ARGB.WHITE)
            surface.fillText(
                "${stageManager.players[1].previousScore / 10}".padStart(6, ' ') + "0",
                22 * Globals.TILE_SIZE + 1,
                top + 3 * Globals.TILE_SIZE + Globals.FONT_REGULAR_CORRECTION,
                ARGB.WHITE,
                Globals.FONT_REGULAR
            )
        }

        surface.fillText(
            "HI" + "${stageManager.highScore / 10}".padStart(6, ' ') + "0",
            11 * Globals.TILE_SIZE + 1,
            top + 3 * Globals.TILE_SIZE + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )
        surface.fillRect(13 * Globals.TILE_SIZE + 1, top + 3 * Globals.TILE_SIZE + 3, 6, 2, ARGB.WHITE)

        surface.draw(11 * Globals.TILE_SIZE, top + 23 * Globals.TILE_SIZE, namco) { dst, src, _, _ ->
            ARGB.rgb(0xB53121).and(src).over(dst)
        }

        surface.draw(4 * Globals.TILE_SIZE, top + 25 * Globals.TILE_SIZE, imageManager.getImage("copyright"))
        surface.fillText(
            NAMCO_LTD,
            6 * Globals.TILE_SIZE + 1,
            top + 25 * Globals.TILE_SIZE + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )
        surface.fillText(
            ".",
            6 * Globals.TILE_SIZE + 1 + NAMCO_LTD.length * Globals.FONT_REGULAR_SIZE - 1,
            top + 25 * Globals.TILE_SIZE + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )
        surface.fillText(
            "ALL RIGHTS RESERVED",
            6 * Globals.TILE_SIZE + 1,
            top + 27 * Globals.TILE_SIZE + Globals.FONT_REGULAR_CORRECTION,
            ARGB.WHITE,
            Globals.FONT_REGULAR
        )

        mainMenuView.draw(surface, top)
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
    }

    fun setMenuItem(index: Int) {
        mainMenu.item = index
    }

    private fun clearCanvas(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)
    }

    override fun destroy() {
        mainMenuController.dispose()
        cursorView.dispose()

        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}