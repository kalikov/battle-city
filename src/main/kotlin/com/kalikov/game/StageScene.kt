package com.kalikov.game

import java.time.Clock

class StageScene(
    private val game: Game,
    private val stageManager: StageManager,
    entityFactory: EntityFactory,
    clock: Clock
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
        script.enqueue(CurtainFall(curtain, script, clock))
        script.enqueue(Execute {
            stageMessage.isVisible = true
        })
        if (isFirstStage) {
            script.enqueue(StageSelect(game.eventManager, stageManager, script))
        }
        script.enqueue(Execute {
            game.eventManager.fireEvent(SoundManager.Play("stage_start"))
            stageManager.curtainBackground = null
        })
        if (!isFirstStage) {
            script.enqueue(Delay(script, 1300, clock))
        }
        script.enqueue(Execute {
            val level = Level(game, stageManager, entityFactory, clock)
            this.level = level
            script.enqueue(Execute { level.start() })
            stageMessage.isVisible = false
            level.show()
        })
        script.enqueue(CurtainRise(curtain, script, clock))
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
            stageManager.curtainBackground?.let { surface.draw(0, 0, it) }
        }
        curtain.draw(surface)
        stageMessage.draw(surface)
    }

    override fun destroy() {
        level?.dispose()

        LeaksDetector.remove(this)
    }
}