package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.LeaksDetector
import com.kalikov.engine.Scene
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px

class StageScene(
    private val game: BattleCityGame,
    private val menuScene: Scene,
) : Scene {
    private val curtain = Curtain()
    private val script = Script()
    private val stageMessage = StageMessage(game.stageManager)
    private val stageSelect = StageSelect(game.eventManager, game.stageManager, script)

    private var level: Level? = null

    val isReady get() = script.isEmpty

    init {
        LeaksDetector.add(this)

        // Player can't complete the first stage without scoring
        val isFirstStage = game.stageManager.players[0].score == 0
        script.enqueue(CurtainFall(curtain, script, game.clock))
        script.enqueue(Execute {
            stageMessage.isVisible = true
        })
        if (isFirstStage) {
            script.enqueue(stageSelect)
        }
        script.enqueue(Execute {
            game.soundManager.stageStart.play()
            game.stageManager.curtainBackground = null
        })
        if (!isFirstStage) {
            script.enqueue(Delay(script, 1300, game.clock))
        }
        script.enqueue(Execute {
            val level = Level(game, menuScene)
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
            game.stageManager.curtainBackground?.let { surface.draw(px(0), px(0), it) }
        }
        curtain.draw(surface)
        stageMessage.draw(surface)
    }

    override fun activate() {
    }

    override fun deactivate() {
        destroy()
    }

    override fun destroy() {
        level?.dispose()

        stageSelect.dispose()

        LeaksDetector.remove(this)
    }
}