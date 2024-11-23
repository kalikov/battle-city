package com.kalikov.game

import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import java.awt.image.BufferedImage
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
open class MainMenuSceneBenchmark {
    private lateinit var scene: MainMenuScene
    private lateinit var surface: ScreenSurface

    @Setup(Level.Iteration)
    fun setup() {
        val fonts = TestFonts()

        val screen: Screen = mock {
            on { createSurface(px(anyInt()), px(anyInt())) } doAnswer {
                val image = BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
                AwtScreenSurface(fonts, image)
            }
        }

        val game = mockGame(screen = screen, imageManager = TestImageManager(fonts), clock = TestClock())

        val players = listOf(Player(game), Player(game))
        val stageManager: StageManager = mock {
            on { this.players } doReturn players
            on { highScore } doReturn 20000
        }

        scene = MainMenuScene(game, stageManager)
        scene.arrived()

        val image = BufferedImage(Globals.CANVAS_WIDTH.toInt(), Globals.CANVAS_HEIGHT.toInt(), BufferedImage.TYPE_INT_ARGB)
        surface = AwtScreenSurface(fonts, image)
    }

    @Benchmark
    fun benchmarkUpdate() {
        scene.update()
    }

    @Benchmark
    fun benchmarkDraw() {
        scene.draw(surface)
    }
}