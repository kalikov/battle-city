package com.kalikov.game

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RectTest {
    @Test
    fun `should create using constructor`() {
        val rect = Rect(0, 0, 1, 1)
        assertEquals(0, rect.x)
        assertEquals(0, rect.y)
        assertEquals(1, rect.width)
        assertEquals(1, rect.height)
    }

    @Test
    fun `should intersect equal rects`() {
        val rect1 = (Rect(0, 0, 1, 1))
        val rect2 = (Rect(0, 0, 1, 1))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should not intersect close rects when right is argument`() {
        val rect1 = (Rect(0, 0, 1, 1))
        val rect2 = (Rect(1, 0, 1, 1))
        assertFalse(rect1.intersects(rect2))
    }

    @Test
    fun `should not intersect close rects when left is argument`() {
        val rect1 = (Rect(2, 0, 1, 1))
        val rect2 = (Rect(0, 0, 2, 2))
        assertFalse(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect overlapping rects`() {
        val rect1 = (Rect(1, 0, 1, 1))
        val rect2 = (Rect(0, 0, 2, 2))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rects when right is argument`() {
        val rect1 = (Rect(0, 0, 4, 4))
        val rect2 = (Rect(2, 2, 4, 4))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rects when left is argument`() {
        val rect1 = (Rect(0, 0, 4, 4))
        val rect2 = (Rect(-2, -2, 4, 4))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rects when bottom-right is argument`() {
        val rect1 = (Rect(0, 0, 4, 4))
        val rect2 = (Rect(2, -2, 4, 4))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should intersect intersecting rects when top-left is argument`() {
        val rect1 = (Rect(0, 0, 4, 4))
        val rect2 = (Rect(-2, 2, 4, 4))
        assertTrue(rect1.intersects(rect2))
    }

    @Test
    fun `should contain equal rect`() {
        val rect1 = Rect(0, 0, 10, 10)
        val rect2 = Rect(0, 0, 10, 10)
        assertTrue(rect1.contains(rect2))
    }

    @Test
    fun `should not contain rect intersecting left`() {
        val rect1 = Rect(0, 0, 10, 10)
        val rect2 = Rect(-1, 0, 10, 10)
        assertFalse(rect1.contains(rect2))
    }

    @Test
    fun `should contain inner rect`() {
        val rect1 = Rect(0, 0, 10, 10)
        val rect2 = Rect(3, 3, 3, 3)
        assertTrue(rect1.contains(rect2))
    }

    @Test
    fun `should not contain rect intersecting right`() {
        val rect1 = Rect(0, 0, 10, 10)
        val rect2 = Rect(8, 3, 3, 3)
        assertFalse(rect1.contains(rect2))
    }
}