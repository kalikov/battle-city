package com.kalikov.game

import java.awt.Font
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max

class BasicGame(
    override val config: GameConfig,
    override val screen: Screen,
    private val input: Input,
    audio: Audio
) : Game, EventSubscriber {
    data object Quit : Event()

    override val eventManager = ConcurrentEventManager()

    val soundManager = ConcurrentSoundManager(audio, eventManager)
    override val imageManager = ConcurrentImageManager(screen)
    val sceneManager = SceneManager(eventManager)

    private var fpsLimit = config.fpsLimit

    private val quit = AtomicBoolean()

    private var duration = 0L

    init {
        require(config.fpsLimit > 0)
        eventManager.addSubscriber(this, setOf(Quit::class, Keyboard.KeyPressed::class))
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
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                quit.set(true)
            }
        }
    }

    private fun think() {
        val start = System.nanoTime()
        while (input.pollEvent()?.also { eventManager.fireEvent(it) } != null) {
            // do nothing
        }

        sceneManager.update()

        screen.clear()
        sceneManager.draw(screen.surface)

        if (config.debug) {
            screen.surface.fillText(
                "${duration / 1000000}.".padStart(4, ' ') + "${duration % 1000000 / 1000}ms".padStart(5, '0'),
                0,
                Globals.UNIT_SIZE,
                ARGB.WHITE,
                "${Font.MONOSPACED}-${Globals.UNIT_SIZE}"
            )

            val lastKeyPressed = input.lastKeyPressed
            if (lastKeyPressed != 0) {
                screen.surface.fillText(
                    "$lastKeyPressed".padStart(3, ' '),
                    screen.surface.width - 3 * Globals.UNIT_SIZE,
                    Globals.UNIT_SIZE,
                    ARGB.WHITE,
                    "${Font.MONOSPACED}-${Globals.UNIT_SIZE}"
                )
            }
        }

        if (screen.flip()) {
            duration = System.nanoTime() - start
        }
    }

    fun quit() {
        quit.set(true)
    }

    fun destroy() {
        eventManager.removeSubscriber(this, setOf(Quit::class, Keyboard.KeyPressed::class))

        soundManager.destroy()
        sceneManager.destroy()
        eventManager.destroy()
    }

    override fun notify(event: Event) {
        if (event is Quit) {
            quit()
        }
    }
}