package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify

class CursorControllerTest {
    @Test
    fun `should subscribe`() {
        val game = mockGame()
        val cursor = stubCursor(game)

        val cursorController = CursorController(game.eventManager, cursor, PixelRect(), mock())

        verify(game.eventManager).addSubscriber(
            cursorController,
            setOf(Keyboard.KeyPressed::class, Keyboard.KeyReleased::class)
        )
    }

    @Test
    fun `should build on key press`() {
        val game = mockGame()
        val builder: BuilderHandler = mock()
        val cursor = stubCursor(game, builder)

        val cursorController = CursorController(game.eventManager, cursor, PixelRect(), mock())

        cursorController.notify(Keyboard.KeyPressed(Keyboard.Key.ACTION, 0))
        verify(builder).build(cursor)
        reset(builder)

        cursorController.notify(Keyboard.KeyReleased(Keyboard.Key.ACTION, 0))
        verify(builder, never()).build(cursor)

        cursorController.notify(Keyboard.KeyPressed(Keyboard.Key.ACTION, 0))
        verify(builder).build(cursor)
        reset(builder)

        cursorController.notify(Keyboard.KeyPressed(Keyboard.Key.RIGHT, 0))
        verify(builder).build(cursor)
        reset(builder)

        cursorController.notify(Keyboard.KeyReleased(Keyboard.Key.ACTION, 0))
        cursorController.notify(Keyboard.KeyPressed(Keyboard.Key.RIGHT, 0))
        verify(builder, never()).build(cursor)
    }
}