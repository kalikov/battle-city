package com.kalikov.game

class GameFieldCommonController(
    game: Game,
    gameField: GameField,
    pauseManager: PauseManager,
    mainContainer: SpriteContainer,
    overlayContainer: SpriteContainer,
    base: TilePoint,
) {
    @Suppress("JoinDeclarationAndAssignment")
    private val movementController: MovementController

    private val powerUpFactory: PowerUpFactory
    private val powerUpHandler: PowerUpHandler

    private val shovelHandler: ShovelHandler
    private val freezeHandler: FreezeHandler

    private val pointsFactory: PointsFactory

    private val bulletHandler: BulletHandler
    private val bulletExplosionFactory: BulletExplosionFactory
    private val tankExplosionFactory: TankExplosionFactory
    private val baseExplosionFactory: BaseExplosionFactory

    private val aiControllersContainer: AITankControllerContainer

    init {
        movementController = MovementController(
            game,
            gameField,
            pauseManager,
            mainContainer,
            overlayContainer,
        )

        powerUpFactory = PowerUpFactory(game, overlayContainer, gameField.bounds)
        powerUpHandler = PowerUpHandler(game)

        shovelHandler = ShovelHandler(game, gameField)
        freezeHandler = FreezeHandler(game.eventManager, game.clock)

        pointsFactory = PointsFactory(game, overlayContainer)

        bulletHandler = BulletHandler(game.eventManager, mainContainer)
        bulletExplosionFactory = BulletExplosionFactory(game, overlayContainer)
        tankExplosionFactory = TankExplosionFactory(game, overlayContainer)
        baseExplosionFactory = BaseExplosionFactory(game, overlayContainer)

        val basePosition = base.toPixelPoint().translate(gameField.bounds.x, gameField.bounds.y)

        aiControllersContainer = AITankControllerContainer(
            game.eventManager,
            pauseManager,
            basePosition,
        )
    }

    fun update() {
        movementController.update()

        aiControllersContainer.update()

        shovelHandler.update()
        freezeHandler.update()
    }

    fun dispose() {
        aiControllersContainer.dispose()

        baseExplosionFactory.dispose()
        tankExplosionFactory.dispose()
        bulletExplosionFactory.dispose()
        bulletHandler.dispose()

        pointsFactory.dispose()

        freezeHandler.dispose()
        shovelHandler.dispose()

        powerUpHandler.dispose()
        powerUpFactory.dispose()

        movementController.dispose()
    }
}