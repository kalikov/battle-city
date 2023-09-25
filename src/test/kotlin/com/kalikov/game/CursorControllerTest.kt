package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CursorControllerTest {
    @Test
    fun `should subscribe`() {
        val eventManager: EventManager = mock()

        val cursor = mockCursor(eventManager)
        val cursorController = CursorController(eventManager, cursor, Rect(), mock())
        verify(eventManager).addSubscriber(
            cursorController,
            setOf(Keyboard.KeyPressed::class, Keyboard.KeyReleased::class)
        )
    }

    @Test
    fun `should build on key press`() {
        val eventManager: EventManager = mock()
        val cursor = mockCursor(eventManager)

        val cursorController = CursorController(eventManager, cursor, Rect(), mock())

        cursorController.notify(Keyboard.KeyPressed(Keyboard.Key.SPACE))
        verify(eventManager).fireEvent(isA<Builder.StructureCreated>())
        reset(eventManager)

        cursorController.notify(Keyboard.KeyReleased(Keyboard.Key.SPACE))
        verify(eventManager, never()).fireEvent(isA<Builder.StructureCreated>())

        cursorController.notify(Keyboard.KeyPressed(Keyboard.Key.SPACE))
        verify(eventManager).fireEvent(isA<Builder.StructureCreated>())
        reset(eventManager)

        cursorController.notify(Keyboard.KeyPressed(Keyboard.Key.RIGHT))
        verify(eventManager).fireEvent(isA<Builder.StructureCreated>())
        reset(eventManager)

        cursorController.notify(Keyboard.KeyReleased(Keyboard.Key.SPACE))
        cursorController.notify(Keyboard.KeyPressed(Keyboard.Key.RIGHT))
        verify(eventManager, never()).fireEvent(isA<Builder.StructureCreated>())
    }
}