//package com.kalikov.game
//
//import com.kalikov.engine.ARGB
//import com.kalikov.engine.AwtScreenSurface
//import com.kalikov.engine.Blending
//import com.kalikov.engine.DefaultEventManager
//import com.kalikov.engine.Screen
//import com.kalikov.engine.ScreenSurface
//import com.kalikov.engine.px
//import kotlinx.serialization.ExperimentalSerializationApi
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.json.decodeFromStream
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.params.ParameterizedTest
//import org.junit.jupiter.params.provider.Arguments
//import org.junit.jupiter.params.provider.MethodSource
//import org.mockito.ArgumentMatchers.anyInt
//import org.mockito.kotlin.any
//import org.mockito.kotlin.anyOrNull
//import org.mockito.kotlin.argumentCaptor
//import org.mockito.kotlin.eq
//import org.mockito.kotlin.inOrder
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.verify
//import org.mockito.kotlin.whenever
//import java.awt.image.BufferedImage
//import java.io.File
//import java.io.FileInputStream
//import kotlin.test.assertContains
//import kotlin.test.assertEquals
//import kotlin.test.assertFalse
//
//class GameFieldTest {
//    companion object {
//        @JvmStatic
//        fun stages(): List<Arguments> {
//            return (1..35)
//                .map { "data/stage$it.json" }
//                .map { File(it) }
//                .mapIndexed { index, file -> Arguments.of(index, file) }
//        }
//    }
//
//    private lateinit var fonts: TestFonts
//
//    @BeforeEach
//    fun beforeEach() {
//        fonts = TestFonts()
//    }
//
//    @OptIn(ExperimentalSerializationApi::class)
//    @ParameterizedTest
//    @MethodSource("stages")
//    fun `should draw classic stages correctly`(index: Int, file: File) {
//        val eventManager = DefaultEventManager()
//
//        val imageManager = TestImageManager(fonts)
//
//        val screen: Screen = mock()
//        whenever(screen.createSurface(px(anyInt()), px(anyInt()))).thenAnswer {
//            val image = BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
//            AwtScreenSurface(fonts, image)
//        }
//
//        val game = mockGame(screen = screen, eventManager = eventManager, imageManager = imageManager)
//        val field = GameField(
//            game,
//            mock(),
//            DefaultSpriteContainer(eventManager),
//            DefaultSpriteContainer(eventManager),
//            0.px,
//            0.px,
//        )
//
//        val json = Json
//        val map: StageMapConfig = FileInputStream(file).use {
//            json.decodeFromStream(it)
//        }
//        field.load(map, 1)
//
//        val image = BufferedImage(field.bounds.width.toInt(), field.bounds.height.toInt(), BufferedImage.TYPE_INT_ARGB)
//        field.draw(AwtScreenSurface(fonts, image))
//
//        assertImageEquals("stage${index + 1}.png", image)
//    }
//
//    @Test
//    fun `should build base wall`() {
//        val game = mockGame()
//        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenReturn(mock())
//        whenever(game.imageManager.getImage(any())).thenReturn(mock())
//        whenever(game.imageManager.getImage("wall_brick")).thenReturn(mock())
//
//        val field = GameField(game, mock(), mock(), mock())
//        field.load(
//            StageMapConfig(
//                base = TilePoint(12.tiles, 24.tiles),
//                playerSpawnPoints = emptyList(),
//                enemySpawnPoints = emptyList(),
//            ),
//            0
//        )
//
//        field.buildBrickWall()
//
//        assertEquals(
//            setOf(
//                BrickTile(11.tiles, 23.tiles),
//                BrickTile(11.tiles, 24.tiles),
//                BrickTile(11.tiles, 25.tiles),
//                BrickTile(12.tiles, 23.tiles),
//                BrickTile(13.tiles, 23.tiles),
//                BrickTile(14.tiles, 23.tiles),
//                BrickTile(14.tiles, 24.tiles),
//                BrickTile(14.tiles, 25.tiles),
//            ),
//            field.walls.config.bricks
//        )
//    }
//
//    @Test
//    fun `should destroy base wall unit entirely`() {
//        val game = mockGame()
//        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenReturn(mock())
//        whenever(game.imageManager.getImage(any())).thenReturn(mock())
//        whenever(game.imageManager.getImage("wall_brick")).thenReturn(mock())
//
//        val field = GameField(game, mock(), mock(), mock())
//        field.load(
//            StageMapConfig(
//                base = TilePoint(12.tiles, 24.tiles),
//                playerSpawnPoints = emptyList(),
//                enemySpawnPoints = emptyList(),
//            ),
//            0
//        )
//
//        val destroy = setOf(
//            TilePoint(10.tiles, 25.tiles),
//            TilePoint(10.tiles, 24.tiles),
//            TilePoint(10.tiles, 23.tiles),
//            TilePoint(10.tiles, 22.tiles),
//            TilePoint(11.tiles, 22.tiles),
//            TilePoint(12.tiles, 22.tiles),
//            TilePoint(13.tiles, 22.tiles),
//            TilePoint(14.tiles, 22.tiles),
//            TilePoint(15.tiles, 22.tiles),
//            TilePoint(15.tiles, 23.tiles),
//            TilePoint(15.tiles, 24.tiles),
//            TilePoint(15.tiles, 25.tiles),
//        )
//        destroy.forEach { field.walls.fillBrickTile(it.x, it.y) }
//
//        field.buildBrickWall()
//
//        val config = field.walls.config
//        destroy.forEach { assertFalse(config.bricks.contains(BrickTile(it.x, it.y)), "$it was not expected") }
//
//    }
//
//    @Test
//    fun `should not destroy non-base wall`() {
//        val game = mockGame()
//        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenReturn(mock())
//        whenever(game.imageManager.getImage(any())).thenReturn(mock())
//        whenever(game.imageManager.getImage("wall_brick")).thenReturn(mock())
//
//        val field = GameField(game, mock(), mock(), mock())
//        field.load(
//            StageMapConfig(
//                base = TilePoint(),
//                playerSpawnPoints = emptyList(),
//                enemySpawnPoints = emptyList(),
//            ),
//            0
//        )
//
//        field.walls.fillBrickTile(0.tiles, 0.tiles)
//
//        field.buildBrickWall()
//
//        assertContains(field.walls.config.bricks, BrickTile())
//    }
//
//    @Test
//    fun `should render in correct order`() {
//        val game = mockGame()
//        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenAnswer { mock<ScreenSurface>() }
//        whenever(game.imageManager.getImage(any())).thenAnswer { mock<ScreenSurface>() }
//
//        val mainContainer = mock<SpriteContainer>()
//        val overlayContainer = mock<SpriteContainer>()
//        val field = GameField(game, mock(), mainContainer, overlayContainer)
//        field.load(
//            StageMapConfig(
//                base = TilePoint(),
//                playerSpawnPoints = emptyList(),
//                enemySpawnPoints = emptyList(),
//            ),
//            0
//        )
//
//        val wallsImage = captureImage(mock(), field.walls::draw)
//        val treesImage = captureImage(mock(), field.trees::draw)
//        val groundImage = captureImage(mock(), field.ground::draw)
//        val baseImage = captureImage(mock(), field.base::draw)
//
//        val screenSurface = mock<ScreenSurface>()
//        field.draw(screenSurface)
//
//        val inOrder = inOrder(screenSurface, mainContainer, overlayContainer)
//        inOrder.verify(screenSurface).fillRect(px(anyInt()), px(anyInt()), px(anyInt()), px(anyInt()), ARGB(anyInt()))
//        inOrder.verify(screenSurface).draw(px(anyInt()), px(anyInt()), eq(groundImage), anyOrNull<Blending>())
//        inOrder.verify(screenSurface).draw(px(anyInt()), px(anyInt()), eq(wallsImage), anyOrNull<Blending>())
//        inOrder.verify(screenSurface).draw(
//            px(anyInt()),
//            px(anyInt()),
//            eq(baseImage),
//            px(anyInt()),
//            px(anyInt()),
//            px(anyInt()),
//            px(anyInt()),
//            anyOrNull<Blending>()
//        )
//        inOrder.verify(mainContainer).forEach(any())
//        inOrder.verify(screenSurface).draw(px(anyInt()), px(anyInt()), eq(treesImage), anyOrNull<Blending>())
//        inOrder.verify(overlayContainer).forEach(any())
//        inOrder.verifyNoMoreInteractions()
//    }
//
//    private fun captureImage(surfaceMock: ScreenSurface, action: (ScreenSurface) -> Unit): ScreenSurface {
//        action(surfaceMock)
//
//        val imageCaptor = argumentCaptor<ScreenSurface>()
//        try {
//            verify(surfaceMock).draw(
//                px(anyInt()),
//                px(anyInt()),
//                imageCaptor.capture(),
//                anyOrNull<Blending>()
//            )
//        } catch (_: Error) {
//            verify(surfaceMock).draw(
//                px(anyInt()),
//                px(anyInt()),
//                imageCaptor.capture(),
//                px(anyInt()),
//                px(anyInt()),
//                px(anyInt()),
//                px(anyInt()),
//                anyOrNull<Blending>()
//            )
//        }
//
//        return imageCaptor.firstValue
//    }
//}