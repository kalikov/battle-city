package com.kalikov.game

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.mock
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import java.time.Clock

class GameFieldTest {
    companion object {
        @JvmStatic
        fun stages(): List<Arguments> {
            return (1..35)
                .map { "data/stage$it.json" }
                .map { File(it) }
                .mapIndexed { index, file -> Arguments.of(index, file) }
        }
    }

    private lateinit var fonts: TestFonts

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @ParameterizedTest
    @MethodSource("stages")
    fun `should draw classic stages correctly`(index: Int, file: File) {
        val eventManager = ConcurrentEventManager()
        val imageManager = TestImageManager(fonts)
        val clock: Clock = mock()
        val field = GameField(
            eventManager,
            imageManager,
            DefaultEntityFactory(eventManager, imageManager, clock),
            ConcurrentSpriteContainer(eventManager),
            0,
            0
        )

        val json = Json
        val map: StageMapConfig = FileInputStream(file).use {
            json.decodeFromStream(it)
        }
        field.load(map, 1)

        val image = BufferedImage(field.bounds.width, field.bounds.height, BufferedImage.TYPE_INT_ARGB)
        field.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("stage${index + 1}.png", image)
    }
}