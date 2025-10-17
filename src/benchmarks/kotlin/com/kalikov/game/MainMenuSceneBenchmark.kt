//package com.kalikov.game
//
//import org.openjdk.jmh.annotations.Benchmark
//import org.openjdk.jmh.annotations.BenchmarkMode
//import org.openjdk.jmh.annotations.Level
//import org.openjdk.jmh.annotations.Mode
//import org.openjdk.jmh.annotations.OutputTimeUnit
//import org.openjdk.jmh.annotations.Scope
//import org.openjdk.jmh.annotations.Setup
//import org.openjdk.jmh.annotations.State
//import java.awt.image.BufferedImage
//import java.time.Clock
//import java.util.concurrent.TimeUnit
//
//@BenchmarkMode(Mode.Throughput)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@State(Scope.Thread)
//open class MainMenuSceneBenchmark {
//    private lateinit var scene: MainMenuScene
//    private lateinit var surface: ScreenSurface
//
//    @Setup(Level.Iteration)
//    fun setup() {
//        val fonts = TestFonts()
//
//        val screen: Screen = object : Screen {
//            override val surface: ScreenSurface get() = TODO("Not yet implemented")
//
//            override fun clear() = Unit
//
//            override fun flip() = true
//
//            override fun createSurface(): ScreenSurface = TODO("Not yet implemented")
//
//            override fun createSurface(width: Pixel, height: Pixel): ScreenSurface {
//                val image = BufferedImage(width.toInt(), height.toInt(), BufferedImage.TYPE_INT_ARGB)
//                return AwtScreenSurface(fonts, image)
//            }
//
//            override fun createSurface(path: String): ScreenSurface = TODO("Not yet implemented")
//        }
//
//        val game = object : Game {
//            override val config: GameConfig get() = TODO("Not yet implemented")
//            override val clock: Clock = TestClock()
//            override val screen = screen
//            override val eventManager: EventManager get() = TODO("Not yet implemented")
//            override val imageManager = TestImageManager(fonts)
//            override val soundManager: SoundManager get() = TODO("Not yet implemented")
//        }
//
//        val players = listOf(Player(game), Player(game))
//        val stageManager = object : StageManager {
//            override val players: List<Player> get() = players
//            override val highScore: Int get() = 20000
//
//            override val stage: Stage
//                get() = TODO("Not yet implemented")
//            override val stageNumber: Int
//                get() = TODO("Not yet implemented")
//            override val demoStage: Stage?
//                get() = TODO("Not yet implemented")
//            override var constructionMap: StageMapConfig
//                get() = TODO("Not yet implemented")
//                set(value) {}
//            override var curtainBackground: ScreenSurface?
//                get() = TODO("Not yet implemented")
//                set(value) {}
//
//            override fun init(stages: List<Stage>, defaultConstructionMap: StageMapConfig, demoStage: Stage?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun setPlayersCount(playersCount: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun reset() {
//                TODO("Not yet implemented")
//            }
//
//            override fun resetConstruction() {
//                TODO("Not yet implemented")
//            }
//
//            override fun next(loop: Boolean) {
//                TODO("Not yet implemented")
//            }
//
//            override fun prev(loop: Boolean) {
//                TODO("Not yet implemented")
//            }
//
//            override fun dispose() {
//                TODO("Not yet implemented")
//            }
//
//        }
//
//        scene = MainMenuScene(game, stageManager)
//        scene.arrived()
//
//        val image = BufferedImage(Globals.CANVAS_WIDTH.toInt(), Globals.CANVAS_HEIGHT.toInt(), BufferedImage.TYPE_INT_ARGB)
//        surface = AwtScreenSurface(fonts, image)
//    }
//
//    @Benchmark
//    fun benchmarkUpdate() {
//        scene.update()
//    }
//
//    @Benchmark
//    fun benchmarkDraw() {
//        scene.draw(surface)
//    }
//}