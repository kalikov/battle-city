package com.kalikov.game

import com.kalikov.engine.AwtInput
import com.kalikov.engine.AwtScreen
import com.kalikov.engine.BasicSceneManager
import com.kalikov.engine.ConcurrentAwtFontManager
import com.kalikov.engine.DefaultEventManager
import com.kalikov.engine.JavaAudio
import com.kalikov.engine.LeaksDetector
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.awt.Graphics
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.FileInputStream
import javax.swing.JFrame

class Main {
    private companion object {
        @JvmStatic
        @OptIn(ExperimentalSerializationApi::class)
        fun main(args: Array<String>) {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val config: GameConfig = FileInputStream(File(Globals.DATA_DIR, "config.json")).use {
                json.decodeFromStream(it)
            }

            val frame = object : JFrame("Battle City") {
                override fun paint(gfx: Graphics) {
                }
            }

            val resolution = config.resolution
//        val taskbar = Taskbar.getTaskbar()
//        taskbar.iconImage = ImageIO.read(File("/Users/aleksandr/Projects/battle-city/src/test/resources/tank_player_up_c0_t1.png"))

            frame.background = AwtScreen.SCREEN_BG_COLOR
//        iconImage = taskbar.iconImage
            frame.setSize(resolution.width.toInt(), resolution.height.toInt())
            frame.ignoreRepaint = true
            frame.isVisible = true
            frame.focusTraversalKeysEnabled = false

            frame.insets.let {
                frame.setSize(
                    resolution.width.toInt() + it.horizontal,
                    resolution.height.toInt() + it.vertical
                )
            }
            frame.createBufferStrategy(2)

            val fontManager = ConcurrentAwtFontManager()
            val screen = AwtScreen(frame, fontManager)
            val input = AwtInput(frame, config.keyboard)
            val audio = JavaAudio()

            val eventManager = DefaultEventManager()
            val sceneManager = BasicSceneManager()
            val game = MainGame(config, input, screen, eventManager, sceneManager, audio)

            val sceneFactory = MainSceneProvider(game, fontManager)
            sceneManager.setNextScene(sceneFactory.loadingScene)

            frame.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(event: WindowEvent) {
                    game.quit()
                }
            })

            frame.addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent) {
                    if (screen.resize()) {
                        eventManager.fireEvent(ResizeEvent)
                    }
                }
            })
            game.loop()
            suppress { sceneManager.destroy() }
            suppress { sceneFactory.destroy() }
            suppress { game.destroy() }
            suppress { eventManager.destroy() }
            suppress { audio.destroy() }
            suppress { input.destroy() }
            suppress { screen.destroy() }
            LeaksDetector.print()
//            exitProcess(0)
        }

        private fun suppress(action: () -> Unit) {
            try {
                action()
            } catch (e: Throwable) {
                e.printStackTrace(System.err)
            }
        }
    }
}