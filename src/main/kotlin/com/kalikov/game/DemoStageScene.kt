package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.Keyboard
import com.kalikov.engine.LeaksDetector
import com.kalikov.engine.Scene
import com.kalikov.engine.ScreenSurface
import java.util.EnumSet

//object MainDemoStageScene : DemoStageScene(MainGame, MainMenuScene)

open class DemoStageScene(
    private val game: BattleCityGame,
    private val exitScene: Scene,
    private val nextScene: Scene,
) : Scene, EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf(
            Keyboard.KeyPressed::class,
            BaseExplosion.Destroyed::class,
            Player.OutOfLives::class,
            EnemyFactory.LastEnemyDestroyed::class,
        )
    }

    private val mainContainer: SpriteContainer
    private val overlayContainer: SpriteContainer

    private val gameField: GameField
    private val gameFieldController: GameFieldCommonController

    private val enemyFactory: EnemyFactory

    private val enemyFactoryView: EnemyFactoryView

    private val livesView: LivesView

    private val stageNumberView: StageNumberView

    private val players: List<Player>
    private val playersTankControllers: List<AIPlayerTankController>
    private val playersTankFactories: List<PlayerTankFactory>

    init {
        LeaksDetector.add(this)

        players = List(2) { i ->
            Player(game, index = i)
        }

        mainContainer = DefaultSpriteContainer(game.eventManager)
        overlayContainer = DefaultSpriteContainer(game.eventManager)

        val demoStage = game.stageManager.demoStage

        gameField = GameField(game, NoopPauseManager, mainContainer, overlayContainer)
        gameFieldController = GameFieldCommonController(
            game,
            gameField,
            NoopPauseManager,
            mainContainer,
            overlayContainer,
            demoStage.map.base
        )
        gameField.load(demoStage.map, players.size)

        enemyFactory = EnemyFactory(
            game,
            NoopPauseManager,
            mainContainer,
            demoStage.map.enemySpawnPoints.map {
                it.toPixelPoint().translate(gameField.bounds.x, gameField.bounds.y)
            },
            demoStage.enemies,
            demoStage.enemySpawnDelay
        )

        playersTankControllers = players.map { player ->
            AIPlayerTankController(game, player, gameField)
        }
        playersTankFactories = players.mapIndexed { index, player ->
            val factory = PlayerTankFactory(
                game,
                NoopPauseManager,
                mainContainer,
                demoStage.map.playerSpawnPoints[index].toPixelPoint().translate(gameField.bounds.x, gameField.bounds.y),
                player,
                EnumSet.of(PlayerTankOption.FRIENDLY_FIRE_INVINCIBLE)
            )
            factory.init(player.upgradeLevel)
            factory
        }

        enemyFactoryView = EnemyFactoryView(
            game.imageManager,
            enemyFactory,
            gameField.bounds.right + 1 + Globals.TILE_SIZE,
            gameField.bounds.y + Globals.TILE_SIZE
        )

        livesView = LivesView(
            game,
            players,
            gameField.bounds.right + 1 + t(1).toPixel(),
            gameField.bounds.bottom + 1 - t(11).toPixel()
        )

        stageNumberView = StageNumberView(
            game.imageManager,
            30,
            gameField.bounds.right + 1 + t(1).toPixel(),
            gameField.bounds.bottom + 1 - t(5).toPixel()
        )
    }

    override fun update() {
        playersTankControllers.forEach { it.update() }

        gameField.update()
        gameFieldController.update()

        enemyFactory.update()
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.rgb(0x808080))

        gameField.draw(surface)

        playersTankControllers.forEach { it.draw(surface) }

        enemyFactoryView.draw(surface)

        livesView.draw(surface)
        stageNumberView.draw(surface)
    }

    override val identity: Int
        get() = TODO("Not yet implemented")

    override fun notify(event: Event) {
        when (event) {
            is Keyboard.KeyPressed -> if (event.playerIndex == 0) {
                keyPressed(event.key)
            }

            is Player.OutOfLives -> onPlayerOutOfLives()
            is BaseExplosion.Destroyed -> stopDemo(false)
            is EnemyFactory.LastEnemyDestroyed -> stopDemo(false)
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

    override fun activate() {
        game.soundManager.enabled = false

        game.eventManager.addSubscriber(this, subscriptions)

        players.forEach {
            it.reset()
            it.activate()
        }
    }

    override fun deactivate() {
        players.forEach { it.deactivate() }

        game.eventManager.removeSubscriber(this, subscriptions)

        game.soundManager.enabled = true
    }

    override fun destroy() {
        livesView.dispose()

        playersTankFactories.forEach { it.dispose() }
        playersTankControllers.forEach { it.dispose() }

        enemyFactory.dispose()

        gameFieldController.dispose()
        gameField.dispose()

        mainContainer.dispose()
        overlayContainer.dispose()

        LeaksDetector.remove(this)
    }
}