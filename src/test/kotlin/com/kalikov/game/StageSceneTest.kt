package com.kalikov.game

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream

class StageSceneTest {
    private lateinit var fonts: TestFonts
    private lateinit var clock: TestClock

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
        clock = TestClock()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `should draw scene correctly`() {
        val eventManager: EventManager = mock()
        val stageManager: StageManager = mock()
        val imageManager = TestImageManager(fonts)

        val player = Player(eventManager, initialScore = 100)
        whenever(stageManager.players).thenReturn(listOf(player))

        val json = Json { ignoreUnknownKeys = true }
        val map: StageMapConfig = FileInputStream(File("data/stage1.json")).use {
            json.decodeFromStream(it)
        }
        val stage = Stage(
            map,
            1,
            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 19))
        )
        whenever(stageManager.stage).thenReturn(stage)
        whenever(stageManager.stageNumber).thenReturn(1)

        val entityFactory = DefaultEntityFactory(eventManager, imageManager, clock)
        val scene = StageScene(mock(), eventManager, imageManager, stageManager, entityFactory, clock)

        while (!scene.isReady) {
            clock.tick(1)
            scene.update()
        }

        val image = BufferedImage(Globals.CANVAS_WIDTH, Globals.CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB)
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage.png", image)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `should draw two players scene correctly`() {
        val eventManager: EventManager = mock()
        val stageManager: StageManager = mock()
        val imageManager = TestImageManager(fonts)

        val playerOne = Player(eventManager, initialScore = 100)
        val playerTwo = Player(eventManager, initialScore = 6000)
        whenever(stageManager.players).thenReturn(listOf(playerOne, playerTwo))

        val json = Json { ignoreUnknownKeys = true }
        val map: StageMapConfig = FileInputStream(File("data/stage1.json")).use {
            json.decodeFromStream(it)
        }
        val stage = Stage(
            map,
            1,
            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 19))
        )
        whenever(stageManager.stage).thenReturn(stage)
        whenever(stageManager.stageNumber).thenReturn(1)

        val entityFactory = DefaultEntityFactory(eventManager, imageManager, clock)
        val scene = StageScene(mock(), eventManager, imageManager, stageManager, entityFactory, clock)

        while (!scene.isReady) {
            clock.tick(1)
            scene.update()
        }

        val image = BufferedImage(Globals.CANVAS_WIDTH, Globals.CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB)
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage_two_players.png", image)
    }

    @Test
    fun `should draw game over message correctly`() {
        val eventManager = ConcurrentEventManager()
        val imageManager = TestImageManager(fonts)
        val stageManager: StageManager = mock()

        val player = Player(eventManager, initialScore = 100)
        whenever(stageManager.players).thenReturn(listOf(player))

        val map = StageMapConfig(emptyList(), Point(12, 24), listOf(Point(8, 24)), emptyList())
        val stage = Stage(map, 1, emptyList())
        whenever(stageManager.stage).thenReturn(stage)
        whenever(stageManager.stageNumber).thenReturn(1)

        val scene = StageScene(mock(), eventManager, imageManager, stageManager, mock(), clock)

        while (!scene.isReady) {
            clock.tick(1)
            scene.update()
        }
        eventManager.fireEvent(BaseExplosion.Destroyed(BaseExplosion(eventManager, imageManager, clock)))
        scene.update() // start initial delay
        clock.tick(1000)
        scene.update() // complete initial delay
        scene.update() // start game over message
        clock.tick(2000)
        scene.update() // update game over message

        val image = BufferedImage(Globals.CANVAS_WIDTH, Globals.CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB)
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage_game_over.png", image)
    }
}