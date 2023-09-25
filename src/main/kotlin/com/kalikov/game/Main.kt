package com.kalikov.game

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
import java.time.Clock
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

            val frame = object : JFrame(config.name) {
                override fun paint(gfx: Graphics) {
                }
            }
            frame.background = AwtScreen.SCREEN_BG_COLOR
            frame.setSize(config.resolution.width, config.resolution.height)
            frame.ignoreRepaint = true
            frame.isVisible = true
            frame.focusTraversalKeysEnabled = false

            val insets = frame.insets
            frame.setSize(
                config.resolution.width + insets.horizontal,
                config.resolution.height + insets.vertical
            )
            frame.createBufferStrategy(2)

            val fontManager = ConcurrentAwtFontManager()

            val screen = AwtScreen(frame, fontManager)
            val input = AwtInput(frame)
            val audio = JavaAudio()
            val game = Game(config, screen, input, audio)

            val clock = Clock.systemUTC()
            val stageManager = DefaultStageManager(game.eventManager)
            val entityFactory = DefaultEntityFactory(game.eventManager, game.imageManager, clock)

            game.sceneManager.setScene {
                LoadingScene(
                    config,
                    screen,
                    game.eventManager,
                    game.imageManager,
                    game.soundManager,
                    fontManager,
                    stageManager,
                    entityFactory,
                    clock
                )
            }

            frame.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(event: WindowEvent) {
                    game.quit()
                }
            })
            frame.addComponentListener(object : ComponentAdapter() {
                override fun componentResized(e: ComponentEvent) {
                    if (screen.resize()) {
                        game.eventManager.fireEvent(ResizeEvent)
                    }
                }
            })
            game.loop()
            suppress { stageManager.dispose() }
            suppress { game.destroy() }
            suppress { audio.destroy() }
            suppress { input.destroy() }
            suppress { screen.destroy() }
            LeaksDetector.print()
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