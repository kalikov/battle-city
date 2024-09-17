package com.kalikov.game

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PixelRectTest {
    @Test
    fun `should create using constructor`() {
        val rect = PixelRect(px(0), px(0), px(1), px(1))
        assertEquals(px(0), rect.x)
        assertEquals(px(0), rect.y)
        assertEquals(px(1), rect.width)
        assertEquals(px(1), rect.height)
    }

    @Test
    fun `should intersect equal rects`() {
        val rect1 = PixelRect(px(0), px(0), px(1), px(1))
        val rect2 = PixelRect(px(0), px(0), px(1), px(1))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should not intersect close rects when right is argument`() {
        val rect1 = PixelRect(px(0), px(0), px(1), px(1))
        val rect2 = PixelRect(px(1), px(0), px(1), px(1))
        assertFalse(rect1.intersects(rect2))
    }

    @Test
    fun `should not intersect close rects when left is argument`() {
        val rect1 = PixelRect(px(2), px(0), px(1), px(1))
        val rect2 = PixelRect(px(0), px(0), px(2), px(2))
        assertFalse(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect overlapping rects`() {
        val rect1 = PixelRect(px(1), px(0), px(1), px(1))
        val rect2 = PixelRect(px(0), px(0), px(2), px(2))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rects when right is argument`() {
        val rect1 = PixelRect(px(0), px(0), px(4), px(4))
        val rect2 = PixelRect(px(2), px(2), px(4), px(4))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rects when left is argument`() {
        val rect1 = PixelRect(px(0), px(0), px(4), px(4))
        val rect2 = PixelRect(px(-2), px(-2), px(4), px(4))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rects when bottom-right is argument`() {
        val rect1 = PixelRect(px(0), px(0), px(4), px(4))
        val rect2 = PixelRect(px(2), px(-2), px(4), px(4))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rects when top-left is argument`() {
        val rect1 = PixelRect(px(0), px(0), px(4), px(4))
        val rect2 = PixelRect(px(-2), px(2), px(4), px(4))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should contain equal rect`() {
        val rect1 = PixelRect(px(0), px(0), px(10), px(10))
        val rect2 = PixelRect(px(0), px(0), px(10), px(10))
        assertTrue(rect1.contains(rect2))
    }

    @Test
    fun `should not contain rect intersecting left`() {
        val rect1 = PixelRect(px(0), px(0), px(10), px(10))
        val rect2 = PixelRect(px(-1), px(0), px(10), px(10))
        assertFalse(rect1.contains(rect2))
    }

    @Test
    fun `should contain inner rect`() {
        val rect1 =PixelRect(px(0), px(0), px(10), px(10))
        val rect2 = PixelRect(px(3), px(3), px(3), px(3))
        assertTrue(rect1.contains(rect2))
    }

    @Test
    fun `should not contain rect intersecting right`() {
        val rect1 = PixelRect(px(0), px(0), px(10), px(10))
        val rect2 = PixelRect(px(8), px(3), px(3), px(3))
        assertFalse(rect1.contains(rect2))
    }
}