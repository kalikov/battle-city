package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class BuilderTest {
    private lateinit var eventManager: EventManager
    private lateinit var builder: Builder
    private lateinit var cursor: Cursor

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        builder = Builder(eventManager, mock(), mock())
        cursor = mockCursor(eventManager = eventManager, builder = builder)
    }

    @Test
    fun `should build brick wall right`() {
        cursor.setPosition(Point(2, 3))
        builder.build(cursor)
        assertEquals(Builder.Structure.BRICK_WALL_RIGHT, builder.structure)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(2, event.sprites.size)
        assertIs<BrickWall>(event.sprites[0])
        assertIs<BrickWall>(event.sprites[1])
        assertEquals(Point(2 + cursor.width / 2, 3), event.sprites[0].position)
        assertEquals(Point(2 + cursor.width / 2, 3 + cursor.height / 2), event.sprites[1].position)
    }

    @Test
    fun `should build brick wall bottom`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.BRICK_WALL_BOTTOM.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(2, event.sprites.size)
        assertIs<BrickWall>(event.sprites[0])
        assertIs<BrickWall>(event.sprites[1])
        assertEquals(Point(2, 3 + cursor.width / 2), event.sprites[0].position)
        assertEquals(Point(2 + cursor.width / 2, 3 + cursor.width / 2), event.sprites[1].position)
    }

    @Test
    fun `should build brick wall left`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.BRICK_WALL_LEFT.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(2, event.sprites.size)
        assertIs<BrickWall>(event.sprites[0])
        assertIs<BrickWall>(event.sprites[1])
        assertEquals(Point(2, 3), event.sprites[0].position)
        assertEquals(Point(2, 3 + cursor.height / 2), event.sprites[1].position)
    }

    @Test
    fun `should build brick wall top`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.BRICK_WALL_TOP.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(2, event.sprites.size)
        assertIs<BrickWall>(event.sprites[0])
        assertIs<BrickWall>(event.sprites[1])
        assertEquals(Point(2, 3), event.sprites[0].position)
        assertEquals(Point(2 + cursor.width / 2, 3), event.sprites[1].position)
    }

    @Test
    fun `should build brick wall full`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.BRICK_WALL_FULL.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(4, event.sprites.size)
        assertIs<BrickWall>(event.sprites[0])
        assertIs<BrickWall>(event.sprites[1])
        assertIs<BrickWall>(event.sprites[2])
        assertIs<BrickWall>(event.sprites[3])
        assertEquals(Point(2, 3), event.sprites[0].position)
        assertEquals(Point(2 + cursor.width / 2, 3), event.sprites[1].position)
        assertEquals(Point(2, 3 + cursor.height / 2), event.sprites[2].position)
        assertEquals(Point(2 + cursor.width / 2, 3 + cursor.height / 2), event.sprites[3].position)
    }

    @Test
    fun `should build steel wall right`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.STEEL_WALL_RIGHT.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(2, event.sprites.size)
        assertIs<SteelWall>(event.sprites[0])
        assertIs<SteelWall>(event.sprites[1])
        assertEquals(Point(2 + cursor.width / 2, 3), event.sprites[0].position)
        assertEquals(Point(2 + cursor.width / 2, 3 + cursor.height / 2), event.sprites[1].position)
    }

    @Test
    fun `should build steel wall bottom`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.STEEL_WALL_BOTTOM.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(2, event.sprites.size)
        assertIs<SteelWall>(event.sprites[0])
        assertIs<SteelWall>(event.sprites[1])
        assertEquals(Point(2, 3 + cursor.height / 2), event.sprites[0].position)
        assertEquals(Point(2 + cursor.width / 2, 3 + cursor.height / 2), event.sprites[1].position)
    }

    @Test
    fun `should build steel wall left`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.STEEL_WALL_LEFT.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(2, event.sprites.size)
        assertIs<SteelWall>(event.sprites[0])
        assertIs<SteelWall>(event.sprites[1])
        assertEquals(Point(2, 3), event.sprites[0].position)
        assertEquals(Point(2, 3 + cursor.height / 2), event.sprites[1].position)
    }

    @Test
    fun `should build steel wall top`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.STEEL_WALL_TOP.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(2, event.sprites.size)
        assertIs<SteelWall>(event.sprites[0])
        assertIs<SteelWall>(event.sprites[1])
        assertEquals(Point(2, 3), event.sprites[0].position)
        assertEquals(Point(2 + cursor.width / 2, 3), event.sprites[1].position)
    }

    @Test
    fun `should build steel wall full`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.STEEL_WALL_FULL.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(4, event.sprites.size)
        assertIs<SteelWall>(event.sprites[0])
        assertIs<SteelWall>(event.sprites[1])
        assertIs<SteelWall>(event.sprites[2])
        assertIs<SteelWall>(event.sprites[3])
        assertEquals(Point(2, 3), event.sprites[0].position)
        assertEquals(Point(2 + cursor.width / 2, 3), event.sprites[1].position)
        assertEquals(Point(2, 3 + cursor.height / 2), event.sprites[2].position)
        assertEquals(Point(2 + cursor.width / 2, 3 + cursor.height / 2), event.sprites[3].position)
    }

    @Test
    fun `should build water`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.WATER.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(1, event.sprites.size)
        assertIs<Water>(event.sprites[0])
        assertEquals(Point(2, 3), event.sprites[0].position)
    }

    @Test
    fun `should build trees`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.TREES.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(1, event.sprites.size)
        assertIs<Trees>(event.sprites[0])
        assertEquals(Point(2, 3), event.sprites[0].position)
    }

    @Test
    fun `should build ice`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.ICE.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(1, event.sprites.size)
        assertIs<Ice>(event.sprites[0])
        assertEquals(Point(2, 3), event.sprites[0].position)
    }

    @Test
    fun `should clear`() {
        cursor.setPosition(Point(2, 3))

        for (i in 0 until Builder.Structure.CLEAR.ordinal) {
            builder.nextStructure()
        }
        builder.build(cursor)

        verify(eventManager).fireEvent(Builder.StructureCreated(emptyList(), cursor))
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
}