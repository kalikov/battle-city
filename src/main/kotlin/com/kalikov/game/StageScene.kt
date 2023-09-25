package com.kalikov.game

import java.time.Clock

class StageScene(
    screen: Screen,
    private val eventManager: EventManager,
    imageManager: ImageManager,
    private val stageManager: StageManager,
    entityFactory: EntityFactory,
    clock: Clock
) : Scene {
    private val curtain = Curtain()
    private val stageMessage = StageMessage(stageManager)
    private val script = Script()

    private var level: Level? = null

    init {
        LeaksDetector.add(this)

        val isFirstStage = stageManager.player.score == 0
        script.enqueue(CurtainFall(curtain, script, clock))
        script.enqueue(Execute {
            stageMessage.visible = true
        })
        if (isFirstStage) {
            script.enqueue(StageSelect(eventManager, stageManager, script))
        }
        script.enqueue(Execute {
            eventManager.fireEvent(SoundManager.Play("stage_start"))
            stageManager.curtainBackground = null
        })
        if (!isFirstStage) {
            script.enqueue(Delay(script, 1500, clock))
        }
        script.enqueue(Execute {
            val level = Level(screen, eventManager, imageManager, stageManager, entityFactory, clock)
            this.level = level
            script.enqueue(level)
            stageMessage.visible = false
            level.show()
        })
        script.enqueue(CurtainRise(curtain, script, clock))
    }

    val isReady get() = script.size <= 1

    override fun update() {
        script.update()
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