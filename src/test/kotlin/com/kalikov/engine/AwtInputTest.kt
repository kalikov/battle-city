package com.kalikov.engine

import com.kalikov.game.KeyEventConfig
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import java.awt.Component
import java.awt.event.KeyEvent
import kotlin.test.assertEquals

class AwtInputTest {
    @Test
    fun `should push events on key press`() {
        val component: Component = mock()
        val input = AwtInput(
            component,
            mapOf(
                "0x26" to KeyEventConfig(Keyboard.Key.UP, 1),
                "0x57" to KeyEventConfig(Keyboard.Key.UP, 2),
            )
        )

        input.keyPressed(
            KeyEvent(
                component,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_UP,
                KeyEvent.CHAR_UNDEFINED,
            )
        )

        assertEquals(Keyboard.KeyPressed(Keyboard.Key.UP, 0), input.pollEvent())

        input.keyPressed(
            KeyEvent(
                component,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0,
                KeyEvent.VK_W,
                'w',
            )
        )

        assertEquals(Keyboard.KeyPressed(Keyboard.Key.UP, 1), input.pollEvent())
    }
}