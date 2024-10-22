package com.kalikov.game

import java.util.EnumSet

class DemoStageScene(
    private val game: Game,
    private val stageManager: StageManager,
    private val mainMenuItem: Int,
    demoStage: Stage,
) : Scene, EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            Keyboard.KeyPressed::class,
            BaseExplosion.Destroyed::class,
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

        game.soundManager.enabled = false

        game.eventManager.addSubscriber(this, subscriptions)

        players = List(2) { i ->
            Player(game.eventManager, index = i)
        }

        mainContainer = DefaultSpriteContainer(game.eventManager)
        overlayContainer = DefaultSpriteContainer(game.eventManager)

        val pauseManager = NoopPauseManager()

        gameField = GameField(game, mainContainer, overlayContainer)
        gameFieldController = GameFieldCommonController(
            game,
            gameField,
            pauseManager,
            mainContainer,
            overlayContainer,
            demoStage.map.base
        )
        gameField.load(demoStage.map, players.size)

        enemyFactory = EnemyFactory(
            game,
            pauseManager,
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
                pauseManager,
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
            game.imageManager,
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

    override fun notify(event: Event) {
        when (event) {
            is Keyboard.KeyPressed -> if (event.playerIndex == 0) {
                keyPressed(event.key)
            }

            is BaseExplosion.Destroyed -> stopDemo(false)
            is EnemyFactory.LastEnemyDestroyed -> stopDemo(false)
            else -> Unit
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (key == Keyboard.Key.START || key == Keyboard.Key.SELECT) {
            stopDemo(true)
        }
    }

    private fun stopDemo(arrived: Boolean) {
        game.eventManager.fireEvent(Scene.Start {
            val mainMenu = MainMenuScene(game, stageManager)
            mainMenu.setMenuItem(mainMenuItem)
            if (arrived) {
                mainMenu.arrived()
            }
            mainMenu
        })
    }

    override fun destroy() {
        playersTankFactories.forEach { it.dispose() }
        playersTankControllers.forEach { it.dispose() }

        players.forEach { it.dispose() }

        enemyFactory.dispose()

        gameFieldController.dispose()
        gameField.dispose()

        mainContainer.dispose()
        overlayContainer.dispose()

        game.eventManager.removeSubscriber(this, subscriptions)

        game.soundManager.enabled = true

        LeaksDetector.remove(this)
    }
}