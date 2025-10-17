package com.kalikov.game

import com.kalikov.engine.AwtScreenSurface
import com.kalikov.engine.DefaultEventManager
import com.kalikov.engine.px
import com.kalikov.util.TestClock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream

class StageSceneTest {
    private lateinit var fonts: TestFonts
    private lateinit var clock: TestClock

    private lateinit var game: BattleCityGame

    private lateinit var stageManager: StageManager

    private lateinit var image: BufferedImage

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
        clock = TestClock()

        game = mockGame(eventManager = DefaultEventManager(), imageManager = TestImageManager(fonts), clock = clock)
        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenAnswer {
            val image = BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
            AwtScreenSurface(fonts, image)
        }

        stageManager = game.stageManager

        image = BufferedImage(Globals.CANVAS_WIDTH.toInt(), Globals.CANVAS_HEIGHT.toInt(), BufferedImage.TYPE_INT_ARGB)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `should draw scene correctly`() {
        val player = Player(game, initialScore = 100)
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
        whenever(stageManager.stageMap).thenReturn(stage.map)
        whenever(stageManager.stageEnemies).thenReturn(stage.enemies)
        whenever(stageManager.stageEnemySpawnDelay).thenReturn(stage.enemySpawnDelay)
        whenever(stageManager.stageNumber).thenReturn(1)

        val scene = StageScene(game, mock())

        while (!scene.isReady) {
            clock.tick(1)
            scene.update()
        }

        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage.png", image)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `should draw two players scene correctly`() {
        val playerOne = Player(game, initialScore = 100)
        val playerTwo = Player(game, initialScore = 6000)
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
        whenever(stageManager.stageMap).thenReturn(stage.map)
        whenever(stageManager.stageEnemies).thenReturn(stage.enemies)
        whenever(stageManager.stageEnemySpawnDelay).thenReturn(stage.enemySpawnDelay)
        whenever(stageManager.stageNumber).thenReturn(1)

        val scene = StageScene(game, mock())

        while (!scene.isReady) {
            clock.tick(1)
            scene.update()
        }

        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage_two_players.png", image)
    }

    @Test
    fun `should draw game over message correctly`() {
        val player = Player(game, initialScore = 100)
        whenever(stageManager.players).thenReturn(listOf(player))

        val map = StageMapConfig(
            base = TilePoint(t(12), t(24)),
            playerSpawnPoints = listOf(TilePoint(t(8), t(24))),
            enemySpawnPoints = emptyList(),
        )
        val stage = Stage(map, 1, emptyList())
        whenever(stageManager.stageMap).thenReturn(stage.map)
        whenever(stageManager.stageEnemies).thenReturn(stage.enemies)
        whenever(stageManager.stageEnemySpawnDelay).thenReturn(stage.enemySpawnDelay)
        whenever(stageManager.stageNumber).thenReturn(1)

        val scene = StageScene(game, mock())

        while (!scene.isReady) {
            clock.tick(1)
            scene.update()
        }
        game.eventManager.fireEvent(BaseExplosion.Destroyed(BaseExplosion(game, mock())))
        scene.update() // start initial delay
        clock.tick(1000)
        scene.update() // complete initial delay
        scene.update() // start game over message
        clock.tick(2000)
        scene.update() // update game over message

        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage_game_over.png", image)
    }
}