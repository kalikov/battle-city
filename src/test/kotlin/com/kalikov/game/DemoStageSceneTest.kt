package com.kalikov.game

import com.kalikov.engine.AwtScreenSurface
import com.kalikov.engine.px
import com.kalikov.util.TestClock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream

class DemoStageSceneTest {
    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun `should draw scene correctly`() {
        val fonts = TestFonts()
        val clock = TestClock()
        val game = mockGame(imageManager = TestImageManager(fonts), clock = clock)
        whenever(game.screen.createSurface(anyInt().px, anyInt().px)).thenAnswer {
            val image = BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
            AwtScreenSurface(fonts, image)
        }

        val json = Json { ignoreUnknownKeys = true }
        val map: StageMapConfig = FileInputStream(File("data/demo.json")).use {
            json.decodeFromStream(it)
        }
        val enemies = listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 20))

        whenever(game.stageManager.stageMap).thenReturn(map)
        whenever(game.stageManager.stageEnemies).thenReturn(enemies)
        val scene = DemoStageScene(game, mock(), mock())
        scene.activate()

        scene.update()

        val image = BufferedImage(
            Globals.CANVAS_WIDTH.toInt(),
            Globals.CANVAS_HEIGHT.toInt(),
            BufferedImage.TYPE_INT_ARGB
        )
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("demo_stage.png", image)
    }
}