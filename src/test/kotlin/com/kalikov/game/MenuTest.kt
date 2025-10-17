package com.kalikov.game

import com.kalikov.engine.Menu
import com.kalikov.engine.MenuItem
import org.junit.jupiter.api.Test
import org.mockito.kotlin.UseConstructor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MenuTest {
    @Test
    fun `should default to first item`() {
        val item1 = mock<MenuItem>()
        val item2 = mock<MenuItem>()
        val menu = Menu(item1, item2)
        assertTrue(menu.isCurrent(item1))
        assertFalse(menu.isCurrent(item2))
        assertSame(item1, menu.getCurrentItem())
    }

    @Test
    fun `should move to next item in a loop`() {
        val item1 = mock<MenuItem>()
        val item2 = mock<MenuItem>()
        val menu = Menu(item1, item2)
        assertSame(item1, menu.getCurrentItem())
        menu.nextItem()
        assertSame(item2, menu.getCurrentItem())
        menu.nextItem()
        assertSame(item1, menu.getCurrentItem())
    }

    @Test
    fun `should execute current item`() {
        val item = mock<MenuItem>()
        val menu = Menu(item)
        menu.executeCurrentItem()
        verify(item).execute()
    }

    @Test
    fun `should get items info`() {
        val item1 = mock<MenuItem>(useConstructor = UseConstructor.withArguments("item1"))
        val item2 = mock<MenuItem>(useConstructor = UseConstructor.withArguments("item2"))
        val menu = Menu(item1, item2)
        assertEquals(2, menu.getItemsCount())
        assertSame(item1, menu.getItem(0))
        assertSame(item2, menu.getItem(1))
        assertTrue(menu.isCurrent(item1))
        assertFalse(menu.isCurrent(item2))
    }
}