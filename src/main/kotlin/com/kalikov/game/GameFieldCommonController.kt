package com.kalikov.game

import com.kalikov.engine.ScreenSurface

class GameFieldCommonController(
    game: BattleCityGame,
    gameField: GameField,
    pauseManager: PauseManager,
    playerTanksManager: PlayerTanksManager,
    enemyTanksManager: EnemyTanksManager,
) {
    @Suppress("JoinDeclarationAndAssignment")
    private val powerUpManager: GamePowerUpManager

    private val movementController: MovementController

    private val shovelHandler: ShovelHandler
    private val freezeHandler: FreezeHandler

    private val pointsManager: GamePointsManager

    private val bulletsManager: GameBulletsManager
    private val explosionsManager: GameExplosionsManager

    private val aiControllersContainer: AITankControllerContainer

    init {
        powerUpManager = GamePowerUpManager(game, pauseManager, enemyTanksManager, gameField.bounds)

        shovelHandler = ShovelHandler(game, pauseManager, gameField)
        freezeHandler = FreezeHandler(game.eventManager, pauseManager, game.clock)

        pointsManager = GamePointsManager(game, pauseManager)

        bulletsManager = GameBulletsManager(game)
        explosionsManager = GameExplosionsManager(game, pauseManager)

        movementController = MovementController(
            game,
            gameField,
            pauseManager,
            bulletsManager,
            playerTanksManager,
            enemyTanksManager,
            powerUpManager,
        )

        aiControllersContainer = AITankControllerContainer(
            game,
            pauseManager,
            playerTanksManager,
            gameField.bounds,
        )
    }

    fun activate() {
        powerUpManager.activate()
        shovelHandler.activate()
        freezeHandler.activate()
        pointsManager.activate()
        bulletsManager.activate()
        explosionsManager.activate()
        movementController.activate()

        aiControllersContainer.activate()
    }

    fun deactivate() {
        aiControllersContainer.deactivate()

        movementController.deactivate()
        explosionsManager.deactivate()
        bulletsManager.deactivate()
        pointsManager.deactivate()
        freezeHandler.deactivate()
        shovelHandler.deactivate()
        powerUpManager.deactivate()
    }

    fun update() {
        bulletsManager.update()
        explosionsManager.update()
        powerUpManager.update()
        pointsManager.update()

        movementController.update()

        aiControllersContainer.update()

        shovelHandler.update()
        freezeHandler.update()
    }

    fun drawContent(surface: ScreenSurface) {
        bulletsManager.draw(surface)
    }

    fun drawOverlay(surface: ScreenSurface) {
        explosionsManager.draw(surface)
        powerUpManager.draw(surface)
        pointsManager.draw(surface)
    }

    fun dispose() {
        aiControllersContainer.dispose()

        explosionsManager.dispose()
        bulletsManager.dispose()

        pointsManager.dispose()

        powerUpManager.dispose()
    }
}