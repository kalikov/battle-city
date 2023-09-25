package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.kotlin.UseConstructor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MainMenuTest {
    @Test
    fun `should default to first item`() {
        val item1 = mock<MainMenuItem>()
        val item2 = mock<MainMenuItem>()
        val menu = MainMenu(item1, item2)
        assertTrue(menu.isCurrent(item1))
        assertFalse(menu.isCurrent(item2))
        assertSame(item1, menu.getCurrentItem())
    }

    @Test
    fun `should move to next item in a loop`() {
        val item1 = mock<MainMenuItem>()
        val item2 = mock<MainMenuItem>()
        val menu = MainMenu(item1, item2)
        assertSame(item1, menu.getCurrentItem())
        menu.nextItem()
        assertSame(item2, menu.getCurrentItem())
        menu.nextItem()
        assertSame(item1, menu.getCurrentItem())
    }

    @Test
    fun `should execute current item`() {
        val item = mock<MainMenuItem>()
        val menu = MainMenu(item)
        menu.executeCurrentItem()
        verify(item).execute()
    }

    @Test
    fun `should get items info`() {
        val item1 = mock<MainMenuItem>(useConstructor = UseConstructor.withArguments("item1"))
        val item2 = mock<MainMenuItem>(useConstructor = UseConstructor.withArguments("item2"))
        val menu = MainMenu(item1, item2)
        assertContentEquals(
            arrayOf(
                MainMenuItemInfo("item1", true),
                MainMenuItemInfo("item2", false),
            ),
            menu.getItemsInfo()
        )
    }
}