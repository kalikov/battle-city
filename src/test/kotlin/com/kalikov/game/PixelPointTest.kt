package com.kalikov.game

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PixelPointTest {
    @Test
    fun `should create using no-arg constructor`() {
        val point = PixelPoint()
        assertEquals(px(0), point.x)
        assertEquals(px(0), point.y)
        assertEquals(PixelPoint(), point)
    }

    @Test
    fun `should create using coordinates as arguments`() {
        val point = PixelPoint(px(1), px(2))
        assertEquals(px(1), point.x)
        assertEquals(px(2), point.y)
    }

    @Test
    fun `should translate`() {
        val point = PixelPoint(px(10), px(100))
        val result = point.translate(px(-10), px(-100))
        assertEquals(PixelPoint(), result)
        assertNotEquals(point, result)
    }

    @Test
    fun `should multiply`() {
        val point = PixelPoint(px(1), px(10))
        val result = point.multiply(10)
        assertEquals(PixelPoint(px(10), px(100)), result)
        assertNotEquals(point, result)
    }

    @Test
    fun `should multiply by coordinate`() {
        val point = PixelPoint(px(1), px(10))
        val result = point.multiply(10, 2)
        assertEquals(PixelPoint(px(10), px(20)), result)
        assertNotEquals(point, result)
    }
}