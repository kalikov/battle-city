package com.kalikov.game

import com.kalikov.engine.px
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.KInOrder
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.test.assertEquals

class BuilderTest {
    private lateinit var gameField: GameFieldHandle
    private lateinit var builder: Builder
    private lateinit var cursor: Cursor

    @BeforeEach
    fun beforeEach() {
        val baseMock: BaseHandle = mock {
            on { bounds } doReturn PixelRect()
        }
        gameField = mock {
            on { bounds } doReturn PixelRect(0.px, 0.px, 100.px, 100.px)
            on { walls } doReturn mock()
            on { ground } doReturn mock()
            on { base } doReturn baseMock
            on { trees } doReturn mock()
        }
        builder = Builder(gameField)
        cursor = Cursor(mockGame(), builder)
    }

    @Test
    fun `should build brick wall right`() {
        cursor.setPosition(0.px, 0.px)
        builder.build(cursor)
        assertEquals(Builder.Structure.BRICK_WALL_RIGHT, builder.structure)

        val order = verifyClear(0.tiles, 0.tiles)
        order.verify(gameField.walls).clearTile(0.tiles, 0.tiles)
        order.verify(gameField.walls).clearTile(0.tiles, 1.tiles)
        order.verify(gameField.walls).fillBrickTile(1.tiles, 0.tiles)
        order.verify(gameField.walls).fillBrickTile(1.tiles, 1.tiles)
        order.verifyNoMoreInteractions()
    }

    @Test
    fun `should build brick wall bottom`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.BRICK_WALL_BOTTOM.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillBrickTile(2.tiles, 4.tiles)
        order.verify(gameField.walls).fillBrickTile(3.tiles, 4.tiles)
        order.verify(gameField.walls).clearTile(2.tiles, 3.tiles)
        order.verify(gameField.walls).clearTile(3.tiles, 3.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should build brick wall left`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.BRICK_WALL_LEFT.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillBrickTile(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillBrickTile(2.tiles, 4.tiles)
        order.verify(gameField.walls).clearTile(3.tiles, 3.tiles)
        order.verify(gameField.walls).clearTile(3.tiles, 4.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should build brick wall top`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.BRICK_WALL_TOP.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillBrickTile(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillBrickTile(3.tiles, 3.tiles)
        order.verify(gameField.walls).clearTile(2.tiles, 4.tiles)
        order.verify(gameField.walls).clearTile(3.tiles, 4.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should build brick wall full`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.BRICK_WALL_FULL.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillBrickTile(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillBrickTile(3.tiles, 3.tiles)
        order.verify(gameField.walls).fillBrickTile(2.tiles, 4.tiles)
        order.verify(gameField.walls).fillBrickTile(3.tiles, 4.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should build steel wall right`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.STEEL_WALL_RIGHT.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.walls).clearTile(2.tiles, 3.tiles)
        order.verify(gameField.walls).clearTile(2.tiles, 4.tiles)
        order.verify(gameField.walls).fillSteelTile(3.tiles, 3.tiles)
        order.verify(gameField.walls).fillSteelTile(3.tiles, 4.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should build steel wall bottom`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.STEEL_WALL_BOTTOM.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillSteelTile(2.tiles, 4.tiles)
        order.verify(gameField.walls).fillSteelTile(3.tiles, 4.tiles)
        order.verify(gameField.walls).clearTile(2.tiles, 3.tiles)
        order.verify(gameField.walls).clearTile(3.tiles, 3.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should build steel wall left`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.STEEL_WALL_LEFT.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillSteelTile(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillSteelTile(2.tiles, 4.tiles)
        order.verify(gameField.walls).clearTile(3.tiles, 3.tiles)
        order.verify(gameField.walls).clearTile(3.tiles, 4.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should build steel wall top`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.STEEL_WALL_TOP.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillSteelTile(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillSteelTile(3.tiles, 3.tiles)
        order.verify(gameField.walls).clearTile(2.tiles, 4.tiles)
        order.verify(gameField.walls).clearTile(3.tiles, 4.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should build steel wall full`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.STEEL_WALL_FULL.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillSteelTile(2.tiles, 3.tiles)
        order.verify(gameField.walls).fillSteelTile(3.tiles, 3.tiles)
        order.verify(gameField.walls).fillSteelTile(2.tiles, 4.tiles)
        order.verify(gameField.walls).fillSteelTile(3.tiles, 4.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should build water`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.WATER.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.ground).fillWaterTile(2.tiles, 3.tiles)
        order.verify(gameField.ground).fillWaterTile(2.tiles, 4.tiles)
        order.verify(gameField.ground).fillWaterTile(3.tiles, 3.tiles)
        order.verify(gameField.ground).fillWaterTile(3.tiles, 4.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should build trees`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.TREES.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.trees).fillTile(2.tiles, 3.tiles)
        order.verify(gameField.trees).fillTile(2.tiles, 4.tiles)
        order.verify(gameField.trees).fillTile(3.tiles, 3.tiles)
        order.verify(gameField.trees).fillTile(3.tiles, 4.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should build ice`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.ICE.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verify(gameField.ground).fillIceTile(2.tiles, 3.tiles)
        order.verify(gameField.ground).fillIceTile(2.tiles, 4.tiles)
        order.verify(gameField.ground).fillIceTile(3.tiles, 3.tiles)
        order.verify(gameField.ground).fillIceTile(3.tiles, 4.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should clear`() {
        cursor.setPosition(2.tiles.toPixel(), 3.tiles.toPixel())

        repeat(Builder.Structure.CLEAR.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val order = verifyClear(2.tiles, 3.tiles)
        order.verifyNoMoreInteractions()
        verifyNoMoreInteractions(gameField.walls, gameField.trees, gameField.ground)
    }

    @Test
    fun `should choose next structure`() {
        assertEquals(0, builder.structure.ordinal)
        for (item in Builder.Structure.entries) {
            assertEquals(item, builder.structure)
            builder.nextStructure()
        }
        assertEquals(0, builder.structure.ordinal)
    }

    @Test
    fun `should choose previous structure`() {
        assertEquals(0, builder.structure.ordinal)
        for (item in Builder.Structure.entries.reversed()) {
            builder.prevStructure()
            assertEquals(item, builder.structure)
        }
    }

    private fun verifyClear(x: Tile, y: Tile): KInOrder {
        val order = inOrder(gameField.walls, gameField.trees, gameField.ground)
        order.verify(gameField.walls).clearTile(x, y)
        order.verify(gameField.walls).clearTile(x, y + 1)
        order.verify(gameField.walls).clearTile(x + 1, y)
        order.verify(gameField.walls).clearTile(x + 1, y + 1)
        order.verify(gameField.trees).clearTile(x, y)
        order.verify(gameField.trees).clearTile(x, y + 1)
        order.verify(gameField.trees).clearTile(x + 1, y)
        order.verify(gameField.trees).clearTile(x + 1, y + 1)
        order.verify(gameField.ground).clearTile(x, y)
        order.verify(gameField.ground).clearTile(x, y + 1)
        order.verify(gameField.ground).clearTile(x + 1, y)
        order.verify(gameField.ground).clearTile(x + 1, y + 1)
        return order
    }
}