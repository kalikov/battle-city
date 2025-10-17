//package com.kalikov.game
//
//import kotlinx.serialization.ExperimentalSerializationApi
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.json.decodeFromStream
//import org.openjdk.jmh.annotations.Benchmark
//import org.openjdk.jmh.annotations.BenchmarkMode
//import org.openjdk.jmh.annotations.Level
//import org.openjdk.jmh.annotations.Mode
//import org.openjdk.jmh.annotations.OutputTimeUnit
//import org.openjdk.jmh.annotations.Scope
//import org.openjdk.jmh.annotations.Setup
//import org.openjdk.jmh.annotations.State
//import java.awt.image.BufferedImage
//import java.io.File
//import java.io.FileInputStream
//import java.time.Clock
//import java.util.concurrent.TimeUnit
//
//@BenchmarkMode(Mode.Throughput)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@State(Scope.Thread)
//open class StageSceneBenchmark {
//    private lateinit var scene: StageScene
//    private lateinit var surface: ScreenSurface
//
//    @OptIn(ExperimentalSerializationApi::class)
//    @Setup(Level.Iteration)
//    fun setup() {
//        val fonts = TestFonts()
//        val clock = TestClock()
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
//        val game = object: Game {
//            override val config = GameConfig()
//            override val clock = clock
//            override val screen = screen
//            override val eventManager = ConcurrentEventManager()
//            override val imageManager = TestImageManager(fonts)
//            override val soundManager: SoundManager get() = TODO("Not yet implemented")
//
//        }
//
//        val players = listOf(Player(game, initialScore = 100), Player(game))
//
//        val json = Json { ignoreUnknownKeys = true }
//        val map: StageMapConfig = FileInputStream(File("data/stage1.json")).use {
//            json.decodeFromStream(it)
//        }
//        val stage = Stage(
//            map,
//            1,
//            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 19))
//        )
//
//        val stageManager = object: StageManager  {
//            override val players = players
//            override val stage = stage
//            override val stageNumber = 1
//            override val demoStage = null
//            override var constructionMap: StageMapConfig
//                get() = TODO("Not yet implemented")
//                set(value) {}
//            override var curtainBackground: ScreenSurface?
//                get() = TODO("Not yet implemented")
//                set(value) {}
//            override val highScore: Int = 20000
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
//        scene = StageScene(game, stageManager)
//        while (!scene.isReady) {
//            clock.tick(1)
//            scene.update()
//        }
//
//        val image = BufferedImage(Globals.CANVAS_WIDTH.toInt(), Globals.CANVAS_HEIGHT.toInt(), BufferedImage.TYPE_INT_ARGB)
//        surface = AwtScreenSurface(fonts, image)
//    }
//
//    @Benchmark
//    fun benchmarkDraw() {
//        scene.draw(surface)
//    }
//}