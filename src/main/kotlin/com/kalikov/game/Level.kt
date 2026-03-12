package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.LeaksDetector
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.script.Delay
import com.kalikov.engine.script.Execute
import com.kalikov.engine.script.MoveFn
import com.kalikov.engine.script.MoveHorz
import com.kalikov.engine.script.MoveVert
import com.kalikov.engine.script.Script

class Level(
    private val game: BattleCityGame,
    private val sceneProvider: SceneProvider,
) : EventSubscriber {
    data object GameOver : Event()

    private companion object {
        private val subscriptions = arrayOf(
            Base.Hit::class,
            BaseExplosion.Destroyed::class,
            Player.OutOfLives::class,
            GameEnemyTanksManager.LastEnemyDestroyed::class
        )
    }

    override val identity get() = Globals.IDENTITY_LEVEL

    var visible = false
        private set

    private val pauseListener = PauseListener(game)

    private val playersTankControllers: List<PlayerTankController>
    private val playerTanksManager: GamePlayerTanksManager

    private val enemyTanksManager: GameEnemyTanksManager
    private val enemyFactoryView: EnemyFactoryView

    private val pauseMessageView: PauseMessageView

    private val livesView: LivesView
    private val stageNumberView: StageNumberView

    private val gameOverMessage: GameOverMessage

    private val gameOverScript = Script()
    private val nextStageScript = Script()

    private val playerGameOverScripts = HashMap<Player, Script>()

    private val gameField: GameField
    private val gameFieldController: GameFieldCommonController

    private val content = object : Drawable {
        override fun draw(surface: ScreenSurface) = drawContent(surface)
    }

    private val overlay = object : Drawable {
        override fun draw(surface: ScreenSurface) = drawOverlay(surface)
    }

    private var isCurtainDraw = false

    init {
        LeaksDetector.add(this)

        gameField = GameField(game, pauseListener, content, overlay)

        playerTanksManager = GamePlayerTanksManager(
            game,
            pauseListener,
            game.stageManager.players,
            gameField.bounds,
        )
        playersTankControllers = game.stageManager.players.map { player ->
            PlayerTankController(
                game.eventManager,
                pauseListener,
                playerTanksManager,
                player,
            )
        }

        enemyTanksManager = GameEnemyTanksManager(
            game,
            pauseListener,
            gameField.bounds,
        )

        gameFieldController = GameFieldCommonController(
            game,
            gameField,
            pauseListener,
            playerTanksManager,
            enemyTanksManager,
        )

        enemyFactoryView = EnemyFactoryView(
            game.imageManager,
            enemyTanksManager,
            gameField.bounds.right + 1 + Globals.TILE_SIZE,
            gameField.bounds.y + Globals.TILE_SIZE
        )

        pauseMessageView = PauseMessageView(
            pauseListener,
            gameField.bounds.x + gameField.bounds.width / 2,
            gameField.bounds.y + gameField.bounds.height / 2,
            game.clock
        )

        livesView = LivesView(
            game,
            game.stageManager.players,
            gameField.bounds.right + 1 + t(1).toPixel(),
            gameField.bounds.bottom + 1 - t(11).toPixel()
        )

        stageNumberView = StageNumberView(
            game.imageManager,
            game.stageManager.stageNumber,
            gameField.bounds.right + 1 + t(1).toPixel(),
            gameField.bounds.bottom + 1 - t(5).toPixel()
        )

        gameOverMessage = GameOverMessage()
    }

    fun activate() {
        visible = false
        isCurtainDraw = false

        pauseListener.activate()
        pauseListener.isEnabled = false

        gameField.load(game.stageManager.stageMap, game.stageManager.players.size)

        game.eventManager.addSubscriber(this, subscriptions)

        playerTanksManager.activate()
        playersTankControllers.forEach {
            it.activate()
        }

        game.stageManager.players.forEach {
            it.stageScore.reset()
        }

        enemyTanksManager.enemyCountLimit = 2 * (game.stageManager.players.size + 1)
        enemyTanksManager.activate()

        gameFieldController.activate()

        gameOverMessage.isVisible = false
        game.stageManager.players.asSequence().take(2).forEach {
            val appearPosition = game.stageManager.stageMap.playerSpawnPoints[it.index]
            val script = playerGameOverScripts.computeIfAbsent(it) { Script() }
            script.isActive = false
            script.clear()
            script.enqueue(Delay(script, 640, game.clock))
            script.enqueue(Execute {
                gameOverMessage.y = appearPosition.y.toPixel() + gameField.bounds.x + Globals.TILE_SIZE
                gameOverMessage.x = if (appearPosition.x.toPixel() < gameField.bounds.width / 2) {
                    gameField.bounds.x + Globals.TILE_SIZE
                } else {
                    gameField.bounds.right - Globals.TILE_SIZE * 5 + 1
                }
                gameOverMessage.isVisible = true
            })
            script.enqueue(
                MoveFn(
                    MoveHorz(gameOverMessage),
                    (appearPosition.x.toPixel() + gameField.bounds.x - Globals.TILE_SIZE + 1).toInt(),
                    1536,
                    script,
                    game.clock
                )
            )
            script.enqueue(Delay(script, 5000, game.clock))
            script.enqueue(Execute {
                gameOverMessage.isVisible = false
            })
        }

        gameOverScript.clear()
        gameOverScript.isActive = false
        gameOverScript.enqueue(Execute {
            game.eventManager.fireEvent(GameOver)
        })
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

        nextStageScript.clear()
        nextStageScript.isActive = false
        nextStageScript.enqueue(Delay(nextStageScript, 2500, game.clock))
        nextStageScript.enqueue(Execute {
            game.stageManager.players.forEach {
                it.upgradeLevel = 0
            }
            playerTanksManager.forEach {
                it.player.upgradeLevel = it.upgradeLevel
            }
            game.stageManager.isGameOver = game.stageManager.isGameOver || playerTanksManager.iterateWhile { false }
            if (!game.stageManager.isGameOver) {
                isCurtainDraw = true

                val image = game.screen.createSurface()
                draw(image)

                game.stageManager.curtainBackground = image
            }
            startStageScoreScene()
        })
    }

    fun deactivate() {
        gameField.dispose()
        gameFieldController.deactivate()
        enemyTanksManager.deactivate()
        playersTankControllers.forEach {
            it.deactivate()
        }
        playerTanksManager.deactivate()
        pauseListener.deactivate()
        game.eventManager.removeSubscriber(this, subscriptions)
    }

    fun update() {
        playersTankControllers.forEach { it.update() }

        gameField.update()
        playerTanksManager.update()
        enemyTanksManager.update()
        gameFieldController.update()

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
        stageNumberView.draw(surface)
        gameOverMessage.draw(surface)
    }

    private fun drawContent(surface: ScreenSurface) {
        if (isCurtainDraw) {
            return
        }
        enemyTanksManager.draw(surface)
        playerTanksManager.draw(surface)

        gameFieldController.drawContent(surface)
    }

    private fun drawOverlay(surface: ScreenSurface) {
        if (isCurtainDraw) {
            return
        }
        gameFieldController.drawOverlay(surface)
    }

    fun show() {
        visible = true
    }

    fun start() {
        pauseListener.isEnabled = true
    }

    override fun notify(event: Event) {
        when (event) {
            is Base.Hit -> game.stageManager.isGameOver = true
            is BaseExplosion.Destroyed -> runGameOverScript()
            is Player.OutOfLives -> onPlayerOutOfLives(event.player)
            is GameEnemyTanksManager.LastEnemyDestroyed -> runNextStageScript()
            else -> Unit
        }
    }

    private fun onPlayerOutOfLives(player: Player) {
        if (game.stageManager.players.none { it.lives > 0 }) {
            runGameOverScript()
        } else {
            runPlayerGameOverScript(player)
        }
    }

    private fun runPlayerGameOverScript(player: Player) {
        if (!game.stageManager.isGameOver) {
            playerGameOverScripts[player]?.isActive = true
        }
    }

    private fun runGameOverScript() {
        game.stageManager.isGameOver = true
        if (!nextStageScript.isActive) {
            gameOverScript.isActive = true
            pauseListener.isEnabled = false
        }
    }

    private fun runNextStageScript() {
        if (!gameOverScript.isActive) {
            nextStageScript.isActive = true
            pauseListener.isEnabled = false
        }
    }

    private fun startStageScoreScene() {
        game.sceneManager.setNextScene(sceneProvider.stageScoreScene)
    }

    fun dispose() {
        livesView.dispose()

        enemyTanksManager.dispose()

        playerTanksManager.dispose()
        playersTankControllers.forEach { it.dispose() }

        gameFieldController.dispose()
        gameField.dispose()

        LeaksDetector.remove(this)
    }
}