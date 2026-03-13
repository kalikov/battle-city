package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.Keyboard
import com.kalikov.engine.LeaksDetector
import com.kalikov.engine.Scene
import com.kalikov.engine.ScreenSurface
import java.util.EnumSet

class DemoStageScene(
    private val game: BattleCityGame,
    private val exitScene: Scene,
    private val nextScene: Scene,
) : Scene, EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf(
            Keyboard.KeyPressed::class,
            BaseExplosion.Destroyed::class,
            Player.OutOfLives::class,
            GameEnemyTanksManager.LastEnemyDestroyed::class,
        )
    }

    override val identity get() = Globals.IDENTITY_DEMO_SCENE

    private val gameField: GameField
    private val gameFieldController: GameFieldCommonController

    private val enemyTanksManager: GameEnemyTanksManager

    private val enemyFactoryView: EnemyFactoryView

    private val livesView: LivesView

    private val stageNumberView: StageNumberView

    private val players: List<Player>
    private val playersTankControllers: List<AIPlayerTankController>
    private val playerTanksManager: GamePlayerTanksManager

    private val content = object : Drawable {
        override fun draw(surface: ScreenSurface) = drawContent(surface)
    }

    private val overlay = object : Drawable {
        override fun draw(surface: ScreenSurface) = drawOverlay(surface)
    }

    init {
        LeaksDetector.add(this)

        players = List(2) { i ->
            Player(game, index = i)
        }

        gameField = GameField(
            game,
            NoopPauseManager,
            content,
            overlay,
        )

        enemyTanksManager = GameEnemyTanksManager(
            game,
            NoopPauseManager,
            gameField.bounds,
        )

        playerTanksManager = GamePlayerTanksManager(
            game,
            NoopPauseManager,
            players,
            gameField.bounds,
            EnumSet.of(PlayerTankOption.FRIENDLY_FIRE_INVINCIBLE),
        )

        playersTankControllers = players.map { player ->
            AIPlayerTankController(game, player, gameField, playerTanksManager, enemyTanksManager)
        }

        gameFieldController = GameFieldCommonController(
            game,
            gameField,
            NoopPauseManager,
            playerTanksManager,
            enemyTanksManager,
        )

        enemyFactoryView = EnemyFactoryView(
            game.imageManager,
            enemyTanksManager,
            gameField.bounds.right + 1 + Globals.TILE_SIZE,
            gameField.bounds.y + Globals.TILE_SIZE,
        )

        livesView = LivesView(
            game,
            players,
            gameField.bounds.right + 1 + 1.tiles.toPixel(),
            gameField.bounds.bottom + 1 - 11.tiles.toPixel()
        )

        stageNumberView = StageNumberView(
            game.imageManager,
            30,
            gameField.bounds.right + 1 + 1.tiles.toPixel(),
            gameField.bounds.bottom + 1 - 5.tiles.toPixel()
        )
    }

    override fun activate() {
        game.stageManager.isDemo = true

        game.soundManager.enabled = false

        gameField.load(game.stageManager.stageMap, players.size)

        game.eventManager.addSubscriber(this, subscriptions)

        players.forEach {
            it.reset()
        }

        playerTanksManager.activate()
        playersTankControllers.forEach {
            it.activate()
        }

        enemyTanksManager.activate()

        gameFieldController.activate()
    }

    override fun deactivate() {
        gameFieldController.deactivate()

        enemyTanksManager.deactivate()

        playersTankControllers.forEach {
            it.deactivate()
        }
        playerTanksManager.deactivate()

        gameField.dispose()

        game.eventManager.removeSubscriber(this, subscriptions)

        game.stageManager.isDemo = false
        game.soundManager.enabled = true
    }

    override fun update() {
        playersTankControllers.forEach { it.update() }

        gameField.update()
        playerTanksManager.update()
        enemyTanksManager.update()

        gameFieldController.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.rgb(0x808080))

        gameField.draw(surface)

        playersTankControllers.forEach { it.draw(surface) }

        enemyFactoryView.draw(surface)

        livesView.draw(surface)
        stageNumberView.draw(surface)
    }

    private fun drawContent(surface: ScreenSurface) {
        enemyTanksManager.draw(surface)
        playerTanksManager.draw(surface)

        gameFieldController.drawContent(surface)
    }

    private fun drawOverlay(surface: ScreenSurface) {
        gameFieldController.drawOverlay(surface)
    }

    override fun notify(event: Event) {
        when (event) {
            is Keyboard.KeyPressed -> if (event.playerIndex == 0) {
                keyPressed(event.key)
            }

            is Player.OutOfLives -> onPlayerOutOfLives()
            is BaseExplosion.Destroyed -> stopDemo(false)
            is GameEnemyTanksManager.LastEnemyDestroyed -> stopDemo(false)
            else -> Unit
        }
    }

    private fun onPlayerOutOfLives() {
        if (players.none { it.lives > 0 }) {
            stopDemo(false)
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (key == Keyboard.Key.START || key == Keyboard.Key.SELECT) {
            stopDemo(true)
        }
    }

    private fun stopDemo(exit: Boolean) {
        if (exit) {
            game.sceneManager.setNextScene(exitScene)
        } else {
            game.sceneManager.setNextScene(nextScene)
        }
    }

    override fun destroy() {
        livesView.dispose()

        playerTanksManager.dispose()
        playersTankControllers.forEach { it.dispose() }

        enemyTanksManager.dispose()

        gameFieldController.dispose()

        LeaksDetector.remove(this)
    }
}