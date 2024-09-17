package com.kalikov.game

class ConstructionScene(
    private val game: Game,
    private val stageManager: StageManager,
) : Scene, EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            Keyboard.KeyPressed::class
        )
    }

    @Suppress("JoinDeclarationAndAssignment")
    private val mainContainer: SpriteContainer

    private val overlayContainer: SpriteContainer

    private val cursor: Cursor

    private val gameField: GameField
    private val cursorController: CursorController

    private val basePosition: TilePoint
    private val playerPositions: List<TilePoint>
    private val enemyPositions: List<TilePoint>

    init {
        mainContainer = DefaultSpriteContainer(game.eventManager)
        overlayContainer = DefaultSpriteContainer(game.eventManager)
        gameField = GameField(game, mainContainer, overlayContainer)

        game.eventManager.addSubscriber(this, subscriptions)

        cursor = Cursor(
            game,
            Builder(gameField),
            gameField.bounds.x,
            gameField.bounds.y,
        )
        overlayContainer.addSprite(cursor)

        cursorController = CursorController(game.eventManager, cursor, gameField.bounds, game.clock)

        val map = stageManager.constructionMap
        basePosition = map.base
        playerPositions = map.playerSpawnPoints
        enemyPositions = map.enemySpawnPoints

        gameField.load(map, 0)
    }

    override fun notify(event: Event) {
        if (event is Keyboard.KeyPressed) {
            keyPressed(event.key)
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (key == Keyboard.Key.START) {
            cursor.destroy()
            val surface = game.screen.createSurface(game.screen.surface.width, game.screen.surface.height)
            draw(surface)

            stageManager.constructionMap = createConstructionMapConfig()
            stageManager.curtainBackground = surface
            game.eventManager.fireEvent(Scene.Start {
                val mainMenu = MainMenuScene(game, stageManager)
                mainMenu.setMenuItem(2)
                mainMenu.arrived()
                mainMenu
            })
        }
    }

    private fun createConstructionMapConfig(): StageMapConfig {
        return StageMapConfig(
            gameField.ground.config,
            gameField.walls.config,
            gameField.trees.config,
            basePosition,
            playerPositions,
            enemyPositions
        )
    }

    override fun update() {
        gameField.update()
        cursorController.update()
    }

    override fun draw(surface: ScreenSurface) {
        drawScene(surface)
    }

    private fun drawScene(surface: ScreenSurface) {
        surface.clear(ARGB.rgb(0x808080))

        gameField.draw(surface)
    }

    override fun destroy() {
        cursorController.dispose()

        game.eventManager.removeSubscriber(this, subscriptions)

        gameField.dispose()
        mainContainer.dispose()
        overlayContainer.dispose()
    }
}