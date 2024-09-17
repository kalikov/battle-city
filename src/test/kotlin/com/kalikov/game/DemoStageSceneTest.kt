package com.kalikov.game

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
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
        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenAnswer {
            val image = BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
            AwtScreenSurface(fonts, image)
        }

        val json = Json { ignoreUnknownKeys = true }
        val map: StageMapConfig = FileInputStream(File("data/demo.json")).use {
            json.decodeFromStream(it)
        }
        val stage = Stage(
            map,
            1,
            listOf(EnemyGroupConfig(EnemyTank.EnemyType.BASIC, 19))
        )

        val scene = DemoStageScene(game, stage)

//        while (!scene.isReady) {
//            clock.tick(1)
        scene.update()
//        }

        val image = BufferedImage(
            Globals.CANVAS_WIDTH.toInt(),
            Globals.CANVAS_HEIGHT.toInt(),
            BufferedImage.TYPE_INT_ARGB
        )
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("demo_stage.png", image)
    }
}