package com.kalikov.game

class Level(
    private val game: Game,
    private val stageManager: StageManager,
) : EventSubscriber {
    private companion object {
        private val subscriptions = setOf(
            Base.Hit::class,
            BaseExplosion.Destroyed::class,
            Player.OutOfLives::class,
            EnemyTank.Score::class,
            EnemyFactory.LastEnemyDestroyed::class
        )
    }

    private var visible = false

    private val pauseListener = PauseListener(game.eventManager)

    private val playersTankControllers: List<PlayerTankController>
    private val playersTankFactories: List<PlayerTankFactory>

    private val bulletHandler: BulletHandler
    private val bulletExplosionFactory: BulletExplosionFactory
    private val tankExplosionFactory: TankExplosionFactory
    private val baseExplosionFactory: BaseExplosionFactory
    private val pointsFactory: PointsFactory

    private val freezeHandler: FreezeHandler

    private val aiControllersContainer: AITankControllerContainer

    private val enemyFactory: EnemyFactory
    private val enemyFactoryView: EnemyFactoryView

    private val powerUpFactory: PowerUpFactory

    private val powerUpHandler: PowerUpHandler
    private val shovelHandler: ShovelHandler

    private val pauseMessageView: PauseMessageView

    private val livesView: LivesView

    private val gameOverMessage: GameOverMessage

    private val gameOverScript: Script
    private val nextStageScript: Script

    private val playerGameOverScripts: Map<Player, Script>

    private val statistics = List(stageManager.players.size) { StageScore() }

    private val mainContainer: SpriteContainer
    private val overlayContainer: SpriteContainer
    private val gameField: GameField

    private val movementController: MovementController

    var gameOver = false
        private set

    init {
        LeaksDetector.add(this)

        pauseListener.isActive = false

        game.eventManager.addSubscriber(this, subscriptions)

        mainContainer = DefaultSpriteContainer(game.eventManager)
        overlayContainer = DefaultSpriteContainer(game.eventManager)
        gameField = GameField(game, mainContainer, overlayContainer)

        movementController = MovementController(
            game.eventManager,
            pauseListener,
            gameField.bounds,
            mainContainer,
            overlayContainer,
            gameField,
            game.clock
        )

        playersTankControllers = stageManager.players.map { player ->
            PlayerTankController(game.eventManager, pauseListener, player)
        }

        val stage = stageManager.stage

        playersTankFactories = stageManager.players.mapIndexed { index, player ->
            PlayerTankFactory(
                game,
                pauseListener,
                mainContainer,
                stage.map.playerSpawnPoints[index].toPixelPoint().translate(gameField.bounds.x, gameField.bounds.y),
                player,
            )
        }

        bulletHandler = BulletHandler(game.eventManager, mainContainer)
        bulletExplosionFactory = BulletExplosionFactory(game, overlayContainer)
        tankExplosionFactory = TankExplosionFactory(game, overlayContainer)
        baseExplosionFactory = BaseExplosionFactory(game, overlayContainer)
        pointsFactory = PointsFactory(game, overlayContainer)
        freezeHandler = FreezeHandler(game.eventManager, game.clock)

        val basePosition = stage.map.base.toPixelPoint().translate(gameField.bounds.x, gameField.bounds.y)

        aiControllersContainer = AITankControllerContainer(
            game.eventManager,
            pauseListener,
            basePosition,
        )

        enemyFactory = EnemyFactory(
            game,
            pauseListener,
            mainContainer,
            stage.map.enemySpawnPoints.map {
                it.toPixelPoint().translate(gameField.bounds.x, gameField.bounds.y)
            },
            stage.enemies,
            stage.enemySpawnDelay
        )
        enemyFactory.enemyCountLimit = 2 * (stageManager.players.size + 1)

        enemyFactoryView = EnemyFactoryView(
            game.imageManager,
            enemyFactory,
            gameField.bounds.right + 1 + Globals.TILE_SIZE,
            gameField.bounds.y + Globals.TILE_SIZE
        )

        powerUpFactory = PowerUpFactory(game, overlayContainer, gameField.bounds)

        powerUpHandler = PowerUpHandler(game)

        shovelHandler = ShovelHandler(game, gameField)

        pauseMessageView = PauseMessageView(
            game.eventManager,
            gameField.bounds.x + gameField.bounds.width / 2,
            gameField.bounds.y + gameField.bounds.height / 2,
            game.clock
        )

        livesView = LivesView(
            game.imageManager,
            stageManager.players,
            gameField.bounds.right + 1 + t(1).toPixel(),
            gameField.bounds.bottom + 1 - t(11).toPixel()
        )

        gameOverMessage = GameOverMessage()

        var index = 0
        playerGameOverScripts = stageManager.players.asSequence().take(2).associateWith {
            val appearPosition = playersTankFactories[index].appearPosition
            val script = Script()
            script.isActive = false
            script.enqueue(Delay(script, 640, game.clock))
            script.enqueue(Execute {
                gameOverMessage.y = appearPosition.y + Globals.TILE_SIZE
                gameOverMessage.x = if (appearPosition.x < gameField.bounds.x + gameField.bounds.width / 2) {
                    gameField.bounds.x + Globals.TILE_SIZE
                } else {
                    gameField.bounds.right - Globals.TILE_SIZE * 5 + 1
                }
                gameOverMessage.isVisible = true
            })
            script.enqueue(
                MoveFn(
                    MoveHorz(gameOverMessage),
                    (appearPosition.x - Globals.TILE_SIZE + 1).toInt(),
                    1536,
                    script,
                    game.clock
                )
            )
            script.enqueue(Delay(script, 5000, game.clock))
            script.enqueue(Execute {
                gameOverMessage.isVisible = false
            })
            index++
            script
        }

        gameOverScript = Script()
        gameOverScript.isActive = false
        gameOverScript.enqueue(Delay(gameOverScript, 640, game.clock))
        gameOverScript.enqueue(Execute {
            playerGameOverScripts.values.forEach {
                it.isActive = false
            }
            gameOverMessage.x = gameField.bounds.x + gameField.bounds.width / 2 - Globals.TILE_SIZE * 2 + 1
            gameOverMessage.y = Globals.CANVAS_HEIGHT + t(2).toPixel()
            gameOverMessage.isVisible = true
        })
        gameOverScript.enqueue(
            MoveFn(
                MoveVert(gameOverMessage),
                (Globals.CANVAS_HEIGHT / 2 - Globals.TILE_SIZE + 1).toInt(),
                2000,
                gameOverScript,
                game.clock
            )
        )
        gameOverScript.enqueue(Delay(gameOverScript, 2000, game.clock))
        gameOverScript.enqueue(Execute {
            startStageScoreScene()
        })

        nextStageScript = Script()
        nextStageScript.isActive = false
        nextStageScript.enqueue(Delay(nextStageScript, 2500, game.clock))
        nextStageScript.enqueue(Execute {
            playersTankFactories.forEachIndexed { index, it ->
                stageManager.players[index].upgradeLevel = 0
                it.playerTank?.let { playerTank ->
                    playerTank.destroy()
                    stageManager.players[index].upgradeLevel = playerTank.upgradeLevel
                }
            }
            gameOver = gameOver || playersTankFactories.all { it.playerTank == null }
            if (!gameOver) {
                mainContainer.forEach { it.destroy() }
                overlayContainer.forEach { it.destroy() }

                val image = game.screen.createSurface()
                draw(image)

                stageManager.curtainBackground = image
            }
            startStageScoreScene()
        })

        gameField.load(stageManager.stage.map, stageManager.players.size)
    }

    fun update() {
        gameField.update()
        movementController.update()
        playersTankControllers.forEach { it.update() }
        enemyFactory.update()
        aiControllersContainer.update()
        freezeHandler.update()
        shovelHandler.update()
        pauseMessageView.update()
        gameOverScript.update()
        nextStageScript.update()
        playerGameOverScripts.values.forEach { it.update() }
    }

    fun draw(surface: ScreenSurface) {
        if (!visible) {
            return
        }
        surface.clear(ARGB.rgb(0x808080))

        gameField.draw(surface)
        enemyFactoryView.draw(surface)
        pauseMessageView.draw(surface)
        livesView.draw(surface)
        drawFlag(surface)
        gameOverMessage.draw(surface)
    }

    fun show() {
        visible = true
    }

    fun start() {
        playersTankFactories.forEachIndexed { index, it ->
            val player = stageManager.players[index]
            if (player.lives > 0) {
                it.init(player.upgradeLevel)
            }
        }
        pauseListener.isActive = true
    }

    override fun notify(event: Event) {
        when (event) {
            is Base.Hit -> gameOver = true
            is BaseExplosion.Destroyed -> runGameOverScript()
            is Player.OutOfLives -> onPlayerOutOfLives(event.player)
            is EnemyFactory.LastEnemyDestroyed -> runNextStageScript()
            is EnemyTank.Score -> statistics[event.player.index].increment(event.tank)
            else -> Unit
        }
    }

    private fun onPlayerOutOfLives(player: Player) {
        if (stageManager.players.none { it.lives > 0 }) {
            runGameOverScript()
        } else {
            runPlayerGameOverScript(player)
        }
    }

    private fun runPlayerGameOverScript(player: Player) {
        if (!gameOver) {
            playerGameOverScripts[player]?.isActive = true
        }
    }

    private fun runGameOverScript() {
        gameOver = true
        if (!nextStageScript.isActive) {
            gameOverScript.isActive = true
            pauseListener.isActive = false
        }
    }

    private fun runNextStageScript() {
        if (!gameOverScript.isActive) {
            nextStageScript.isActive = true
            pauseListener.isActive = false
        }
    }

    private fun startStageScoreScene() {
        game.eventManager.fireEvent(
            Scene.Start {
                StageScoreScene(
                    game,
                    stageManager,
                    statistics,
                    gameOver,
                )
            }
        )
    }

    private fun drawFlag(surface: ScreenSurface) {
        val flag = game.imageManager.getImage("flag")
        val x = gameField.bounds.right + 1 + t(1).toPixel()
        val y = gameField.bounds.bottom + 1 - t(5).toPixel()
        surface.draw(x, y, flag)

        val stageNumber = "${stageManager.stageNumber}".padStart(2, ' ')
        surface.fillText(stageNumber, x + 1, y + flag.height + Globals.TILE_SIZE - 1, ARGB.BLACK, Globals.FONT_REGULAR)
    }

    fun dispose() {
        pauseMessageView.dispose()

        shovelHandler.dispose()
        powerUpHandler.dispose()

        powerUpFactory.dispose()

        enemyFactory.dispose()

        aiControllersContainer.dispose()

        freezeHandler.dispose()
        pointsFactory.dispose()
        baseExplosionFactory.dispose()
        tankExplosionFactory.dispose()
        bulletExplosionFactory.dispose()
        bulletHandler.dispose()

        playersTankFactories.forEach { it.dispose() }
        playersTankControllers.forEach { it.dispose() }

        movementController.dispose()

        gameField.dispose()
        mainContainer.dispose()
        overlayContainer.dispose()

        pauseListener.dispose()

        game.eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}