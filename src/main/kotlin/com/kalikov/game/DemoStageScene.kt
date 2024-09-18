package com.kalikov.game

class DemoStageScene(
    private val game: Game,
    private val stageManager: StageManager,
    private val mainMenuItem: Int,
    demoStage: Stage,
) : Scene, EventSubscriber {
    private companion object {
        private val subscriptions = setOf(Keyboard.KeyPressed::class)
    }

    private val spriteContainer: SpriteContainer
    private val overlayContainer: SpriteContainer

    private val gameField: GameField

    init {
        LeaksDetector.add(this)

        game.eventManager.addSubscriber(this, subscriptions)

        spriteContainer = DefaultSpriteContainer(game.eventManager)
        overlayContainer = DefaultSpriteContainer(game.eventManager)

        gameField = GameField(game, spriteContainer, overlayContainer)
        gameField.load(demoStage.map, 2)
    }

    override fun update() {
        gameField.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.rgb(0x808080))

        gameField.draw(surface)
    }

    override fun destroy() {
        spriteContainer.dispose()

        game.eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }

    override fun notify(event: Event) {
        if (event is Keyboard.KeyPressed && event.playerIndex == 0) {
            keyPressed(event.key)
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (key == Keyboard.Key.START || key == Keyboard.Key.SELECT) {
            game.eventManager.fireEvent(Scene.Start {
                val mainMenu = MainMenuScene(game, stageManager)
                mainMenu.setMenuItem(mainMenuItem)
                mainMenu.arrived()
                mainMenu
            })
        }
    }
}