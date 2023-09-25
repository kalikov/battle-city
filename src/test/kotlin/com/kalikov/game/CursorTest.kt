package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class CursorTest {
    private lateinit var eventManager: EventManager
    private lateinit var cursor: Cursor

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        cursor = Cursor(eventManager, mock(), Builder(eventManager, mock(), mock()), mock())
    }

    @Test
    fun `should fire StructureCreated event`() {
        cursor.build()

        val captor = argumentCaptor<Builder.StructureCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val event = captor.firstValue
        assertSame(cursor, event.cursor)
        assertEquals(2, event.sprites.size)
        assertIs<BrickWall>(event.sprites[0])
        assertEquals(Point(Globals.TILE_SIZE, 0), event.sprites[0].position)
        assertIs<BrickWall>(event.sprites[1])
        assertEquals(Point(Globals.TILE_SIZE, Globals.TILE_SIZE), event.sprites[1].position)
    }
}