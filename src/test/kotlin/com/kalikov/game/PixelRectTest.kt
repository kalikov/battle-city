package com.kalikov.game

import com.kalikov.engine.px
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PixelRectTest {
    @Test
    fun `should create using constructor`() {
        val rect = PixelRect(0.px, 0.px, 1.px, 1.px)
        assertEquals(0.px, rect.x)
        assertEquals(0.px, rect.y)
        assertEquals(1.px, rect.width)
        assertEquals(1.px, rect.height)
    }

    @Test
    fun `should intersect equal rectangles`() {
        val rect1 = PixelRect(0.px, 0.px, 1.px, 1.px)
        val rect2 = PixelRect(0.px, 0.px, 1.px, 1.px)
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should not intersect close rectangles when right is argument`() {
        val rect1 = PixelRect(0.px, 0.px, 1.px, 1.px)
        val rect2 = PixelRect(1.px, 0.px, 1.px, 1.px)
        assertFalse(rect1.intersects(rect2))
    }

    @Test
    fun `should not intersect close rectangles when left is argument`() {
        val rect1 = PixelRect(2.px, 0.px, 1.px, 1.px)
        val rect2 = PixelRect(0.px, 0.px, 2.px, 2.px)
        assertFalse(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect overlapping rectangles`() {
        val rect1 = PixelRect(1.px, 0.px, 1.px, 1.px)
        val rect2 = PixelRect(0.px, 0.px, 2.px, 2.px)
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rectangles when right is argument`() {
        val rect1 = PixelRect(0.px, 0.px, 4.px, 4.px)
        val rect2 = PixelRect(2.px, 2.px, 4.px, 4.px)
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rectangles when left is argument`() {
        val rect1 = PixelRect(0.px, 0.px, 4.px, 4.px)
        val rect2 = PixelRect((-2).px, (-2).px, 4.px, 4.px)
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rectangles when bottom-right is argument`() {
        val rect1 = PixelRect(0.px, 0.px, 4.px, 4.px)
        val rect2 = PixelRect(2.px, (-2).px, 4.px, 4.px)
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rectangles when top-left is argument`() {
        val rect1 = PixelRect(0.px, 0.px, 4.px, 4.px)
        val rect2 = PixelRect((-2).px, 2.px, 4.px, 4.px)
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should contain equal rect`() {
        val rect1 = PixelRect(0.px, 0.px, 10.px, 10.px)
        val rect2 = PixelRect(0.px, 0.px, 10.px, 10.px)
        assertTrue(rect1.contains(rect2))
    }

    @Test
    fun `should not contain rect intersecting left`() {
        val rect1 = PixelRect(0.px, 0.px, 10.px, 10.px)
        val rect2 = PixelRect((-1).px, 0.px, 10.px, 10.px)
        assertFalse(rect1.contains(rect2))
    }

    @Test
    fun `should contain inner rect`() {
        val rect1 =PixelRect(0.px, 0.px, 10.px, 10.px)
        val rect2 = PixelRect(3.px, 3.px, 3.px, 3.px)
        assertTrue(rect1.contains(rect2))
    }

    @Test
    fun `should not contain rect intersecting right`() {
        val rect1 = PixelRect(0.px, 0.px, 10.px, 10.px)
        val rect2 = PixelRect(8.px, 3.px, 3.px, 3.px)
        assertFalse(rect1.contains(rect2))
    }
}