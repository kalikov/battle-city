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
//import org.openjdk.jmh.annotations.TearDown
//import java.awt.image.BufferedImage
//import java.util.concurrent.TimeUnit
//
//@BenchmarkMode(Mode.Throughput)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@State(Scope.Thread)
//open class AwtScreenSurfaceBenchmark {
//    private companion object {
//        private val blending = Blending { dst, src, _, _ -> ARGB.rgb(0xB53121).and(src).over(dst) }
//
//        private const val FILL_TEXT = "test"
//
//        private val TARGET_WIDTH = Globals.CANVAS_WIDTH.toInt()
//        private val TARGET_HEIGHT = Globals.CANVAS_HEIGHT.toInt()
//    }
//
//    private lateinit var fonts: TestFonts
//    private lateinit var targetSurface: AwtScreenSurface
//
//    private val fillTextSurface: AwtScreenSurface by lazy {
//        val width = FILL_TEXT.length * Globals.FONT_BIG_SIZE
//        val image = BufferedImage(width.toInt(), Globals.FONT_BIG_CORRECTION.toInt(), BufferedImage.TYPE_INT_ARGB)
//        val surface = AwtScreenSurface(fonts, image)
//        surface.clear(ARGB.TRANSPARENT)
//        surface.fillText(FILL_TEXT, 0.px, Globals.FONT_BIG_CORRECTION, ARGB.rgb(0x888888), Globals.FONT_BIG, blending)
//        surface
//    }
//
//    private lateinit var sourceSurface: AwtScreenSurface
//    private lateinit var sourceLazyBlender: LazyImage
//
//    @Setup(Level.Iteration)
//    fun setup() {
//        fonts = TestFonts()
//        fonts.getFont(Globals.FONT_BIG)
//
//        val targetImage = BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, BufferedImage.TYPE_INT_ARGB)
//        targetSurface = AwtScreenSurface(fonts, targetImage)
//        targetSurface.clear(ARGB.BLACK)
//
//        val sourceImage = BufferedImage(TARGET_WIDTH - 20, TARGET_HEIGHT - 20, BufferedImage.TYPE_INT_ARGB)
//        sourceSurface = AwtScreenSurface(fonts, sourceImage)
//        sourceSurface.clear(ARGB.RED)
//
//        val screen = object : Screen {
//            override val surface = targetSurface
//
//            override fun clear() = Unit
//
//            override fun flip() = true
//
//            override fun createSurface() = createSurface(px(TARGET_WIDTH), px(TARGET_HEIGHT))
//
//            override fun createSurface(width: Pixel, height: Pixel): ScreenSurface {
//                return AwtScreenSurface(fonts, BufferedImage(width.toInt(), height.toInt(), BufferedImage.TYPE_INT_ARGB))
//            }
//
//            override fun createSurface(path: String) = TODO()
//        }
//        sourceLazyBlender = LazyImage.Blender(screen, sourceSurface, blending)
//    }
//
//    @Benchmark
//    fun benchmarkClear() {
//        targetSurface.clear(ARGB.RED)
//    }
//
//    @Benchmark
//    fun benchmarkClearRect() {
//        targetSurface.clear(10.px, 10.px, px(TARGET_WIDTH - 10), px(TARGET_HEIGHT - 10), ARGB(0x66FF0000))
//    }
//
//    @Benchmark
//    fun benchmarkDrawRect() {
//        targetSurface.drawRect(10.px, 10.px, px(TARGET_WIDTH - 10), px(TARGET_HEIGHT - 10), ARGB(0x66FF0000))
//    }
//
//    @Benchmark
//    fun benchmarkFillRect() {
//        targetSurface.fillRect(10.px, 10.px, px(TARGET_WIDTH - 10), px(TARGET_HEIGHT - 10), ARGB(0x66FF0000))
//    }
//
//    @Benchmark
//    fun benchmarkDrawLine() {
//        targetSurface.drawLine(10.px, 10.px, px(TARGET_WIDTH - 10), px(TARGET_HEIGHT - 10), ARGB(0x66FF0000))
//    }
//
//    @Benchmark
//    fun benchmarkFillText() {
//        targetSurface.fillText(FILL_TEXT, 10.px, 10.px + Globals.FONT_BIG_CORRECTION, ARGB.rgb(0x888888), Globals.FONT_BIG)
//    }
//
//    @Benchmark
//    fun benchmarkFillTextBlended() {
//        targetSurface.fillText(
//            FILL_TEXT,
//            10.px,
//            10.px + Globals.FONT_BIG_CORRECTION,
//            ARGB.rgb(0x888888),
//            Globals.FONT_BIG,
//            blending
//        )
//    }
//
//    @Benchmark
//    fun benchmarkFillTextBlendedOptimised() {
//        targetSurface.draw(10.px, 10.px, fillTextSurface)
//    }
//
//    @Benchmark
//    fun benchmarkDrawImage() {
//        targetSurface.draw(10.px, 10.px, sourceSurface)
//    }
//
//    @Benchmark
//    fun benchmarkDrawSubImage() {
//        targetSurface.draw(10.px, 10.px, sourceSurface, 0.px, 0.px, 20.px, 20.px)
//    }
//
//    @Benchmark
//    fun benchmarkDrawImageBlended() {
//        targetSurface.draw(10.px, 10.px, sourceSurface, blending)
//    }
//
//    @Benchmark
//    fun benchmarkDrawImageBlendedOptimised() {
//        targetSurface.draw(10.px, 10.px, sourceLazyBlender.target)
//    }
//
//    @TearDown(Level.Iteration)
//    fun destroy() {
//        sourceSurface.dispose()
//        targetSurface.dispose()
//
//        fillTextSurface.dispose()
//        sourceLazyBlender.dispose()
//    }
//}