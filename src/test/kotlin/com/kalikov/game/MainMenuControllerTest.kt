package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import kotlin.test.assertSame

class MainMenuControllerTest {
    @Test
    fun `should subscribe on key press`() {
        val eventManager: EventManager = mock()
        val controller = MainMenuController(eventManager, MainMenu())
        verify(eventManager).addSubscriber(controller, setOf(Keyboard.KeyPressed::class))
    }

    @Test
    fun `should unsubscribe`() {
        val eventManager: EventManager = mock()
        val controller = MainMenuController(eventManager, MainMenu())
        controller.dispose()
        verify(eventManager).removeSubscriber(controller, setOf(Keyboard.KeyPressed::class))
    }

    @Test
    fun `should move to next item on SELECT key press`() {
        val item1 = mock<MainMenuItem>()
        val item2 = mock<MainMenuItem>()
        val menu = MainMenu(item1, item2)

        val controller = MainMenuController(mock(), menu)
        controller.isActive = true

        controller.notify(Keyboard.KeyPressed(Keyboard.Key.SELECT, 0))

        assertSame(item2, menu.getCurrentItem())
    }

    @Test
    fun `should execute current item on START key press`() {
        val item = mock<MainMenuItem>()
        val controller = MainMenuController(mock(), MainMenu(item))
        controller.isActive = true

        controller.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))

        verify(item).execute()
    }

    @Test
    fun `should not execute current item on START key press when not active`() {
        val item = mock<MainMenuItem>()
        val controller = MainMenuController(mock(), MainMenu(item))

        controller.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))

        verify(item, never()).execute()
    }
}