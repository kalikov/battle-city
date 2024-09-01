package com.kalikov.game

import java.time.Clock

class Level(
    private val screen: Screen,
    private val eventManager: EventManager,
    private val imageManager: ImageManager,
    private val stageManager: StageManager,
    private val entityFactory: EntityFactory,
    private val clock: Clock
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

    private val pauseListener = PauseListener(eventManager)

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

    private val statistics = List(stageManager.players.size) { StageScore() }

    private val spriteContainer: SpriteContainer
    private val gameField: GameField

    private val movementController: MovementController

    var gameOver = false
        private set

    init {
        LeaksDetector.add(this)

        pauseListener.isActive = false

        eventManager.addSubscriber(this, subscriptions)

        spriteContainer = DefaultSpriteContainer(eventManager)
        gameField = GameField(eventManager, imageManager, entityFactory, spriteContainer)

        movementController = MovementController(
            eventManager,
            pauseListener,
            gameField.bounds,
            spriteContainer,
            clock
        )

        playersTankControllerFactories = stageManager.players.map { player ->
            PlayerTankControllerFactory(eventManager, pauseListener, player)
        }

        val stage = stageManager.stage

        playersTankFactories = stageManager.players.mapIndexed { index, player ->
            PlayerTankFactory(
                eventManager,
                imageManager,
                pauseListener,
                spriteContainer,
                stage.map.playerSpawnPoints[index].multiply(Globals.TILE_SIZE)
                    .translate(gameField.bounds.x, gameField.bounds.y),
                clock,
                player,
            )
        }

        bulletFactory = BulletFactory(eventManager, spriteContainer)
        bulletExplosionFactory = BulletExplosionFactory(eventManager, imageManager, spriteContainer, clock)
        tankExplosionFactory = TankExplosionFactory(eventManager, imageManager, spriteContainer)
        baseExplosionFactory = BaseExplosionFactory(eventManager, imageManager, spriteContainer, clock)
        pointsFactory = PointsFactory(eventManager, imageManager, spriteContainer, clock)
        freezeHandler = FreezeHandler(eventManager, clock)

        val basePosition = stage.map.base.multiply(Globals.TILE_SIZE).translate(gameField.bounds.x, gameField.bounds.y)

        aiControllersContainer = AITankControllerContainer(
            eventManager,
            pauseListener,
            basePosition
        )

        enemyFactory = EnemyFactory(
            eventManager,
            imageManager,
            pauseListener,
            spriteContainer,
            stage.map.enemySpawnPoints.map {
                it.multiply(Globals.TILE_SIZE).translate(gameField.bounds.x, gameField.bounds.y)
            },
            clock,
            stage.enemies,
            stage.enemySpawnDelay
        )

        enemyFactoryView = EnemyFactoryView(
            imageManager,
            enemyFactory,
            gameField.bounds.right + 1 + Globals.TILE_SIZE,
            gameField.bounds.y + Globals.TILE_SIZE
        )

        powerUpFactory = PowerUpFactory(eventManager, imageManager, spriteContainer, clock, gameField.bounds)

        baseWallBuilder = BaseWallBuilder(
            eventManager,
            spriteContainer,
            gameField.bounds,
            basePosition
        )

        powerUpHandler = PowerUpHandler(eventManager, imageManager)

        shovelHandler = ShovelHandler(eventManager, imageManager, baseWallBuilder, clock)

        pauseMessageView = PauseMessageView(
            eventManager,
            gameField.bounds.x + gameField.bounds.width / 2,
            gameField.bounds.y + gameField.bounds.height / 2,
            clock
        )

        livesView = LivesView(
            imageManager,
            stageManager.players,
            gameField.bounds.right + 1 + Globals.TILE_SIZE,
            gameField.bounds.bottom + 1 - 5 * Globals.UNIT_SIZE - Globals.TILE_SIZE
        )

        gameOverMessage = GameOverMessage(
            gameField.bounds.x + gameField.bounds.width / 2 - Globals.TILE_SIZE * 2 + 1,
            Globals.CANVAS_HEIGHT + Globals.UNIT_SIZE
        )

        gameOverScript = Script()
        gameOverScript.isActive = false
        gameOverScript.enqueue(Delay(gameOverScript, 640, clock))
        gameOverScript.enqueue(
            MoveFn(
                MoveVert(gameOverMessage),
                Globals.CANVAS_HEIGHT / 2 - Globals.TILE_SIZE + 1,
                2000,
                gameOverScript,
                clock
            )
        )
        gameOverScript.enqueue(Delay(gameOverScript, 2000, clock))
        gameOverScript.enqueue(Execute {
            startStageScoreScene()
        })

        nextStageScript = Script()
        nextStageScript.isActive = false
        nextStageScript.enqueue(Delay(nextStageScript, 2500, clock))
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

                val image = screen.createSurface()
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
            is Player.OutOfLives -> onPlayerOutOfLives()
            is EnemyFactory.LastEnemyDestroyed -> runNextStageScript()
            is EnemyTank.Score -> statistics[event.player.index].increment(event.tank)
            else -> Unit
        }
    }

    private fun onPlayerOutOfLives() {
        if (stageManager.players.none { it.lives > 0 }) {
            runGameOverScript()
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
        eventManager.fireEvent(
            Scene.Start {
                StageScoreScene(
                    screen,
                    eventManager,
                    imageManager,
                    stageManager,
                    entityFactory,
                    statistics,
                    gameOver,
                    clock
                )
            }
        )
    }

    private fun drawFlag(surface: ScreenSurface) {
        val flag = imageManager.getImage("flag")
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

        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}