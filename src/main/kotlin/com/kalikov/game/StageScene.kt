package com.kalikov.game

class StageScene(
    private val game: Game,
    private val stageManager: StageManager,
) : Scene {
    private val curtain = Curtain()
    private val stageMessage = StageMessage(stageManager)
    private val script = Script()

    private var level: Level? = null

    val isReady get() = script.isEmpty

    init {
        LeaksDetector.add(this)

        // Player can't complete the first stage without scoring
        val isFirstStage = stageManager.players[0].score == 0
        script.enqueue(CurtainFall(curtain, script, game.clock))
        script.enqueue(Execute {
            stageMessage.isVisible = true
        })
        if (isFirstStage) {
            script.enqueue(StageSelect(game.eventManager, stageManager, script))
        }
        script.enqueue(Execute {
            game.soundManager.play("stage_start")
            stageManager.curtainBackground = null
        })
        if (!isFirstStage) {
            script.enqueue(Delay(script, 1300, game.clock))
        }
        script.enqueue(Execute {
            val level = Level(game, stageManager)
            this.level = level
            script.enqueue(Execute { level.start() })
            stageMessage.isVisible = false
            level.show()
        })
        script.enqueue(CurtainRise(curtain, script, game.clock))
    }

    override fun update() {
        if (script.isEmpty) {
            level?.update()
        } else {
            script.update()
        }
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)
        if (level != null) {
            level?.draw(surface)
        } else {
            stageManager.curtainBackground?.let { surface.draw(px(0), px(0), it) }
        }
        curtain.draw(surface)
        stageMessage.draw(surface)
    }

    override fun destroy() {
        level?.dispose()

        LeaksDetector.remove(this)
    }
}