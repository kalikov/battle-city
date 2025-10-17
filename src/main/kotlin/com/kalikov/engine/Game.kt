package com.kalikov.engine

import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max
import kotlin.reflect.KClass

abstract class Game(
    private val fpsLimit: Int,
    private val input: Input,
    private val screen: Screen,
    private val eventManager: EventManager,
    private val sceneManager: SceneManager,
) : EventSubscriber {
    private companion object {
        private val subscriptions = arrayOf<KClass<out Event>>(Quit::class)
    }

    data object Quit : Event()

    private val quit = AtomicBoolean()

    init {
        require(fpsLimit > 0)
        eventManager.addSubscriber(this, subscriptions)
    }

    fun loop() {
        while (!quit.get()) {
            val delay = 1000 / fpsLimit
            val startTime = System.currentTimeMillis()

            try {
                think()
            } catch (e: Throwable) {
                e.printStackTrace(System.err)
                return
            }

            val endTime = System.currentTimeMillis()
            try {
                Thread.sleep(max(delay - (endTime - startTime), 1))
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                quit.set(true)
            }
        }
    }

    protected open fun onDraw(surface: ScreenSurface) = Unit

    protected open fun think(): Boolean {
        while (input.pollEvent()?.also { eventManager.fireEvent(it) } != null) {
            // do nothing
        }

        sceneManager.update()

        screen.clear()
        sceneManager.scene?.draw(screen.surface)

        onDraw(screen.surface)

        return screen.flip()
    }

    fun quit() {
        quit.set(true)
    }

    fun destroy() {
        eventManager.removeSubscriber(this, subscriptions)

        onDestroy()
    }

    protected open fun onDestroy() = Unit

    override fun notify(event: Event) {
        if (event is Quit) {
            quit()
        }
    }
}