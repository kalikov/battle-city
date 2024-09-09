package com.kalikov.game

class Level(
    private val game: Game,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
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

    private val playersTankControllerFactories: List<PlayerTankControllerFactory>
    private val playersTankFactories: List<PlayerTankFactory>

    private val bulletFactory: BulletFactory
    private val bulletExplosionFactory: BulletExplosionFactory
    private val tankExplosionFactory: TankExplosionFactory
    private val baseExplosionFactory: BaseExplosionFactory
    private val pointsFactory: PointsFactory

    private val freezeHandler: FreezeHandler

    private val aiControllersContainer: AITankControllerContainer

    private val enemyFactory: EnemyFactory
    private val enemyFactoryView: EnemyFactoryView

    private val powerUpFactory: PowerUpFactory

    private val baseWallBuilder: BaseWallBuilder

    private val powerUpHandler: PowerUpHandler
    private val shovelHandler: ShovelHandler

    private val pauseMessageView: PauseMessageView

    private val livesView: LivesView

    private val gameOverMessage: GameOverMessage

    private val gameOverScript: Script
    private val nextStageScript: Script

    private val playerGameOverScripts: Map<Player, Script>

    private val statistics = List(stageManager.players.size) { StageScore() }

    private val spriteContainer: SpriteContainer
    private val gameField: GameField

    private val movementController: MovementController

    var gameOver = false
        private set

    init {
        LeaksDetector.add(this)

        pauseListener.isActive = false

        game.eventManager.addSubscriber(this, subscriptions)

        spriteContainer = DefaultSpriteContainer(game.eventManager)
        gameField = GameField(game.eventManager, game.imageManager, entityFactory, spriteContainer)

        movementController = MovementController(
            game.eventManager,
            pauseListener,
            gameField.bounds,
            spriteContainer,
            game.clock
        )

        playersTankControllerFactories = stageManager.players.map { player ->
            PlayerTankControllerFactory(game.eventManager, pauseListener, player)
        }

        val stage = stageManager.stage

        playersTankFactories = stageManager.players.mapIndexed { index, player ->
            PlayerTankFactory(
                game,
                pauseListener,
                spriteContainer,
                stage.map.playerSpawnPoints[index].multiply(Globals.TILE_SIZE)
                    .translate(gameField.bounds.x, gameField.bounds.y),
                player,
            )
        }

        bulletFactory = BulletFactory(game.eventManager, spriteContainer)
        bulletExplosionFactory = BulletExplosionFactory(
            game.eventManager,
            game.imageManager,
            spriteContainer,
            game.clock
        )
        tankExplosionFactory = TankExplosionFactory(game, spriteContainer)
        baseExplosionFactory = BaseExplosionFactory(game, spriteContainer)
        pointsFactory = PointsFactory(game.eventManager, game.imageManager, spriteContainer, game.clock)
        freezeHandler = FreezeHandler(game.eventManager, game.clock)

        val basePosition = stage.map.base.multiply(Globals.TILE_SIZE).translate(gameField.bounds.x, gameField.bounds.y)

        aiControllersContainer = AITankControllerContainer(
            game.eventManager,
            pauseListener,
            basePosition
        )

        enemyFactory = EnemyFactory(
            game,
            pauseListener,
            spriteContainer,
            stage.map.enemySpawnPoints.map {
                it.multiply(Globals.TILE_SIZE).translate(gameField.bounds.x, gameField.bounds.y)
            },
            game.clock,
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

        powerUpFactory = PowerUpFactory(game.eventManager, game.imageManager, spriteContainer, game.clock, gameField.bounds)

        baseWallBuilder = BaseWallBuilder(
            game.eventManager,
            spriteContainer,
            gameField.bounds,
            basePosition
        )

        powerUpHandler = PowerUpHandler(game)

        shovelHandler = ShovelHandler(game, baseWallBuilder)

        pauseMessageView = PauseMessageView(
            game.eventManager,
            gameField.bounds.x + gameField.bounds.width / 2,
            gameField.bounds.y + gameField.bounds.height / 2,
            game.clock
        )

        livesView = LivesView(
            game.imageManager,
            stageManager.players,
            gameField.bounds.right + 1 + Globals.TILE_SIZE,
            gameField.bounds.bottom + 1 - 5 * Globals.UNIT_SIZE - Globals.TILE_SIZE
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
                    appearPosition.x - Globals.TILE_SIZE + 1,
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
            gameOverMessage.y = Globals.CANVAS_HEIGHT + Globals.UNIT_SIZE
            gameOverMessage.isVisible = true
        })
        gameOverScript.enqueue(
            MoveFn(
                MoveVert(gameOverMessage),
                Globals.CANVAS_HEIGHT / 2 - Globals.TILE_SIZE + 1,
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
                spriteContainer.forEach { sprite ->
                    sprite.isStatic = true
                    if (sprite is Tank) {
                        sprite.destroy()
                    }
                }

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
        playersTankControllerFactories.forEach { it.update() }
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
                    entityFactory,
                    statistics,
                    gameOver,
                )
            }
        )
    }

    private fun drawFlag(surface: ScreenSurface) {
        val flag = game.imageManager.getImage("flag")
        val x = gameField.bounds.right + 1 + Globals.TILE_SIZE
        val y = gameField.bounds.bottom + 1 - 2 * Globals.UNIT_SIZE - Globals.TILE_SIZE
        surface.draw(x, y, flag)

        val stageNumber = "${stageManager.stageNumber}".padStart(2, ' ')
        surface.fillText(stageNumber, x + 1, y + flag.height + Globals.TILE_SIZE - 1, ARGB.BLACK, Globals.FONT_REGULAR)
    }

    fun dispose() {
        pauseMessageView.dispose()

        shovelHandler.dispose()
        powerUpHandler.dispose()

        baseWallBuilder.dispose()

        powerUpFactory.dispose()

        enemyFactory.dispose()

        aiControllersContainer.dispose()

        freezeHandler.dispose()
        pointsFactory.dispose()
        baseExplosionFactory.dispose()
        tankExplosionFactory.dispose()
        bulletExplosionFactory.dispose()
        bulletFactory.dispose()

        playersTankFactories.forEach { it.dispose() }
        playersTankControllerFactories.forEach { it.dispose() }

        movementController.dispose()

        spriteContainer.dispose()

        pauseListener.dispose()

        game.eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}