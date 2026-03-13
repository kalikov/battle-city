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
//import java.time.Clock
//import java.util.concurrent.TimeUnit
//
//@BenchmarkMode(Mode.Throughput)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@State(Scope.Thread)
//open class EnemyTankBenchmark {
//    private lateinit var tank: EnemyTank
//
//    @Setup(Level.Invocation)
//    fun setup() {
//        val fonts = TestFonts()
//        val pauseManager = object: PauseManager {
//            override val isPaused: Boolean get() = false
//
//        }
//        val screen: Screen = object : Screen {
//            override val surface: ScreenSurface get() = TODO("Not yet implemented")
//
//            override fun clear() = Unit
//
//            override fun flip() = true
//
//            override fun createSurface(): ScreenSurface = TODO("Not yet implemented")
//
//            override fun createSurface(width: Pixel, height: Pixel): ScreenSurface = TODO()
//
//            override fun createSurface(path: String): ScreenSurface = TODO("Not yet implemented")
//        }
//        val game = object : Game {
//            override val config: GameConfig get() = TODO("Not yet implemented")
//            override val clock: Clock = TestClock()
//            override val screen = screen
//            override val eventManager: EventManager get() = TODO("Not yet implemented")
//            override val imageManager = TestImageManager(fonts)
//            override val soundManager: SoundManager get() = TODO("Not yet implemented")
//        }
//        tank = EnemyTank.create(game, pauseManager, 0.px, 0.px, EnemyTank.EnemyType.BASIC)
//    }
//
//    @Benchmark
//    fun benchmarkDestroy() {
//        tank.destroy()
//        tank.update()
//    }
//}