package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.Keyboard
import com.kalikov.engine.Scene
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import kotlin.reflect.KClass

class ConstructionScene(
    private val game: BattleCityGame,
    private val menuScene: Scene,
) : Scene, EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(
            Keyboard.KeyPressed::class
        )
    }

    override val identity get() = Globals.IDENTITY_CONSTRUCTION_SCENE

    @Suppress("JoinDeclarationAndAssignment")
    private val mainContainer: SpriteContainer

    private val overlayContainer: SpriteContainer

    private val cursor: Cursor

    private val gameField: GameField
    private val cursorController: CursorController

    init {
        mainContainer = DefaultSpriteContainer(game.eventManager)
        overlayContainer = DefaultSpriteContainer(game.eventManager)
        gameField = GameField(game, NoopPauseManager, mainContainer, overlayContainer)

        cursor = Cursor(
            game,
            Builder(gameField),
            gameField.bounds.x,
            gameField.bounds.y,
        )
//        overlayContainer.addSprite(cursor)

        cursorController = CursorController(game.eventManager, cursor, gameField.bounds, game.clock)
    }

    override fun notify(event: Event) {
        if (event is Keyboard.KeyPressed) {
            keyPressed(event.key)
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (key == Keyboard.Key.START) {
//            cursor.destroy()
            val surface = game.screen.createSurface(game.screen.surface.width, game.screen.surface.height)
            drawScene(surface)

            game.stageManager.constructionMap = createConstructionMapConfig()
            game.stageManager.curtainBackground = surface

            game.sceneManager.setNextScene(menuScene)
        }
    }

    private fun createConstructionMapConfig(): StageMapConfig {
        val map = game.stageManager.constructionMap
        return StageMapConfig(
            gameField.ground.config,
            gameField.walls.config,
            gameField.trees.config,
            map.base,
            map.playerSpawnPoints,
            map.enemySpawnPoints,
        )
    }

    override fun update() {
        gameField.update()
        cursor.update()
        cursorController.update()
    }

    override fun draw(surface: ScreenSurface) {
        drawScene(surface)

        cursor.draw(surface)
    }

    private fun drawScene(surface: ScreenSurface) {
        surface.clear(ARGB.rgb(0x808080))

        gameField.draw(surface)
    }

    override fun activate() {
        game.eventManager.addSubscriber(this, subscriptions)

        cursorController.activate()

        val map = game.stageManager.constructionMap
        gameField.load(map, 0)
    }

    override fun deactivate() {
        cursorController.deactivate()

        game.eventManager.removeSubscriber(this, subscriptions)

        gameField.dispose()
    }

    override fun destroy() {
        mainContainer.dispose()
        overlayContainer.dispose()
    }
}