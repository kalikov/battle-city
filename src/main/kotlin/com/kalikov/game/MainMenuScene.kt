package com.kalikov.game

import java.time.Clock

class MainMenuScene(
    screen: Screen,
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    stageManager: StageManager,
    entityFactory: EntityFactory,
    clock: Clock
) : Scene, EventSubscriber {
    companion object {
        const val INTERVAL = 16

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

        surface.fillText("BATTLE", 28, top + 76, ARGB.WHITE, Globals.FONT_BIG, brickBlending)
        surface.fillText("CITY", 64, top + 116, ARGB.WHITE, Globals.FONT_BIG, brickBlending)

        surface.draw(18, top + 24, imageManager.getImage("roman_one"))
        surface.fillText("     00", 25, top + 31, ARGB.WHITE, Globals.FONT_REGULAR)
        surface.fillRect(25, top + 27, 6, 2, ARGB.WHITE)

        surface.fillText("HI  20000", 89, top + 31, ARGB.WHITE, Globals.FONT_REGULAR)
        surface.fillRect(105, top + 27, 6, 2, ARGB.WHITE)

        surface.draw(88, top + 184, imageManager.getImage("namco")) { dst, src, _, _ ->
            ARGB.rgb(0xB53121).and(src).over(dst)
        }

        surface.draw(32, top + 200, imageManager.getImage("copyright"))
        surface.fillText("1980 1985 NAMCO LTD", 49, top + 207, ARGB.WHITE, Globals.FONT_REGULAR)
        surface.fillText(".", 200, top + 207, ARGB.WHITE, Globals.FONT_REGULAR)
        surface.fillText("ALL RIGHTS RESERVED", 49, top + 223, ARGB.WHITE, Globals.FONT_REGULAR)

        mainMenuView.draw(surface, top)
    }

    override fun notify(event: Event) {
        if (event is Keyboard.KeyPressed) {
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