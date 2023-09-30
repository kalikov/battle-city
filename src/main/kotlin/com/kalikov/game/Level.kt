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
            Tank.EnemyDestroyed::class,
            EnemyFactory.LastEnemyDestroyed::class
        )
    }

    private var visible = false

    private val pauseListener = PauseListener(eventManager)

    private val playerTankControllerFactory: PlayerTankControllerFactory

    private val playerTankFactory: PlayerTankFactory

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

    private val statistics = StageScore()

    private val spriteContainer: SpriteContainer
    private val gameField: GameField

    private val movementController: MovementController

    var gameOver = false
        private set

    init {
        LeaksDetector.add(this)

        pauseListener.isActive = false

        eventManager.addSubscriber(this, subscriptions)

        spriteContainer = ConcurrentSpriteContainer(eventManager)
        gameField = GameField(eventManager, imageManager, entityFactory, spriteContainer)

        movementController = MovementController(
            eventManager,
            pauseListener,
            gameField.bounds,
            spriteContainer,
            clock
        )

        playerTankControllerFactory = PlayerTankControllerFactory(eventManager, pauseListener)

        val stage = stageManager.stage

        playerTankFactory = PlayerTankFactory(
            eventManager,
            imageManager,
            pauseListener,
            spriteContainer,
            stage.map.playerSpawnPoint.multiply(Globals.TILE_SIZE).translate(gameField.bounds.x, gameField.bounds.y),
            clock
        )

        bulletFactory = BulletFactory(eventManager, spriteContainer)
        bulletExplosionFactory =
            BulletExplosionFactory(eventManager, imageManager, spriteContainer, clock)
        tankExplosionFactory = TankExplosionFactory(eventManager, imageManager, pauseListener, spriteContainer)
        baseExplosionFactory = BaseExplosionFactory(eventManager, imageManager, spriteContainer, clock)
        pointsFactory = PointsFactory(eventManager, imageManager, spriteContainer, clock)
        freezeHandler = FreezeHandler(eventManager, clock)

        aiControllersContainer = AITankControllerContainer(
            eventManager,
            pauseListener,
            stage.map.base.multiply(Globals.TILE_SIZE),
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

        powerUpFactory = PowerUpFactory(eventManager, imageManager, spriteContainer, clock)

        baseWallBuilder = BaseWallBuilder(
            eventManager,
            spriteContainer,
            setOf(
                Point(gameField.bounds.x + 11 * Globals.TILE_SIZE, gameField.bounds.y + 25 * Globals.TILE_SIZE),
                Point(gameField.bounds.x + 11 * Globals.TILE_SIZE, gameField.bounds.y + 24 * Globals.TILE_SIZE),
                Point(gameField.bounds.x + 11 * Globals.TILE_SIZE, gameField.bounds.y + 23 * Globals.TILE_SIZE),
                Point(gameField.bounds.x + 12 * Globals.TILE_SIZE, gameField.bounds.y + 23 * Globals.TILE_SIZE),
                Point(gameField.bounds.x + 13 * Globals.TILE_SIZE, gameField.bounds.y + 23 * Globals.TILE_SIZE),
                Point(gameField.bounds.x + 14 * Globals.TILE_SIZE, gameField.bounds.y + 23 * Globals.TILE_SIZE),
                Point(gameField.bounds.x + 14 * Globals.TILE_SIZE, gameField.bounds.y + 24 * Globals.TILE_SIZE),
                Point(gameField.bounds.x + 14 * Globals.TILE_SIZE, gameField.bounds.y + 25 * Globals.TILE_SIZE),
            )
        )

        powerUpHandler = PowerUpHandler(eventManager, imageManager)

        shovelHandler = ShovelHandler(eventManager, imageManager, baseWallBuilder, clock)

        pauseMessageView = PauseMessageView(eventManager, clock)

        livesView = LivesView(
            imageManager,
            stageManager.player,
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
            val playerTank = playerTankFactory.playerTank
            if (playerTank == null || gameOver) {
                gameOver = true
            } else {
                playerTank.destroy()

                for (sprite in spriteContainer.sprites) {
                    sprite.static = true
                    if (sprite is Tank) {
                        sprite.destroy()
                    }
                }

                val image = screen.createSurface()
                draw(image)

                stageManager.curtainBackground = image
                stageManager.player.upgradeLevel = playerTank.upgradeLevel
            }
            startStageScoreScene()
        })

        gameField.load(stageManager.stage.map)
    }

    fun update() {
        gameField.update()
        movementController.update()
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
        playerTankFactory.init(stageManager.player.upgradeLevel)
        pauseListener.isActive = true
    }

    override fun notify(event: Event) {
        when (event) {
            is Base.Hit -> gameOver = true
            is BaseExplosion.Destroyed -> runGameOverScript()
            is Player.OutOfLives -> runGameOverScript()
            is EnemyFactory.LastEnemyDestroyed -> runNextStageScript()
            is Tank.EnemyDestroyed -> statistics.increment(event.tank)
            else -> Unit
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

        playerTankFactory.dispose()

        playerTankControllerFactory.dispose()

        movementController.dispose()
        gameField.dispose()
        spriteContainer.dispose()

        pauseListener.dispose()

        eventManager.removeSubscriber(this, subscriptions)

        LeaksDetector.remove(this)
    }
}