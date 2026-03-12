package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.Event
import com.kalikov.engine.EventSubscriber
import com.kalikov.engine.Keyboard
import com.kalikov.engine.LeaksDetector
import com.kalikov.engine.Scene
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import com.kalikov.engine.script.Delay
import com.kalikov.engine.script.Execute
import com.kalikov.engine.script.Script
import kotlin.reflect.KClass

class StageScene(
    private val game: BattleCityGame,
    sceneProvider: SceneProvider,
) : Scene, EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(Keyboard.KeyPressed::class)
    }

    override val identity get() = Globals.IDENTITY_STAGE_SCENE

    private val curtain = Curtain()
    private val script = Script()
    private val stageMessage = StageMessage(game.stageManager)

    private val curtainFall = CurtainFall(curtain, script, game.clock)
    private val showStageExecute = Execute {
        stageMessage.isVisible = true
    }
    private val stageSelect = StageSelect()
    private val soundExecute = Execute {
        game.soundManager.stageStart.play()
        game.stageManager.curtainBackground = null
    }
    private val delay = Delay(script, 1300, game.clock)
    private val showLevelExecute = Execute {
        stageMessage.isVisible = false
        level.show()
    }
    private val startLevelExecute = Execute {
        level.start()
    }
    private val curtainRise = CurtainRise(curtain, script, game.clock)

    private val level = Level(game, sceneProvider)

    val isReady get() = script.isDone

    init {
        LeaksDetector.add(this)
    }

    override fun activate() {
        curtain.reset()
        curtainFall.reset()
        curtainRise.reset()
        delay.reset()
        stageSelect.isActive = false
        stageMessage.isVisible = false

        level.activate()

        // Players can't complete the first stage without scoring
        val isFirstStage = game.stageManager.players.all { it.score == 0 }
        script.clear()
        script.enqueue(curtainFall)
        script.enqueue(showStageExecute)
        if (isFirstStage) {
            script.enqueue(stageSelect)
        }
        script.enqueue(soundExecute)
        if (!isFirstStage) {
            script.enqueue(delay)
        }
        script.enqueue(showLevelExecute)
        script.enqueue(curtainRise)
        script.enqueue(startLevelExecute)

        game.eventManager.addSubscriber(this, subscriptions)
    }

    override fun deactivate() {
        game.eventManager.removeSubscriber(this, subscriptions)

        game.soundManager.stageStart.stop()

        level.deactivate()
    }

    override fun notify(event: Event) {
        if (event is Keyboard.KeyPressed) {
            keyPressed(event.key)
        }
    }

    private fun keyPressed(key: Keyboard.Key) {
        if (!stageSelect.isActive) {
            return
        }
        when (key) {
            Keyboard.Key.START -> {
                stageSelect.isActive = false
                script.actionCompleted()
            }

            Keyboard.Key.UP, Keyboard.Key.RIGHT, Keyboard.Key.ACTION -> {
                game.stageManager.next(false)
            }

            Keyboard.Key.DOWN, Keyboard.Key.LEFT -> {
                game.stageManager.prev(false)
            }

            else -> Unit
        }
    }

    override fun update() {
        if (script.isDone) {
            level.update()
        } else {
            script.update()
        }
    }

    override fun draw(surface: ScreenSurface) {
        surface.clear(ARGB.BLACK)
        if (level.visible) {
            level.draw(surface)
        } else {
            game.stageManager.curtainBackground?.let { surface.draw(0.px, 0.px, it) }
        }
        curtain.draw(surface)
        stageMessage.draw(surface)
    }

    override fun destroy() {
        level.dispose()

        LeaksDetector.remove(this)
    }
}