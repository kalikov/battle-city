package com.kalikov.game

import com.kalikov.engine.px
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PixelPointTest {
    @Test
    fun `should create using no-arg constructor`() {
        val point = PixelPoint()
        assertEquals(0.px, point.x)
        assertEquals(0.px, point.y)
        assertEquals(PixelPoint(), point)
    }

    @Test
    fun `should create using coordinates as arguments`() {
        val point = PixelPoint(1.px, 2.px)
        assertEquals(1.px, point.x)
        assertEquals(2.px, point.y)
    }

    @Test
    fun `should translate`() {
        val point = PixelPoint(10.px, 100.px)
        val result = point.translate(-10.px, -100.px)
        assertEquals(PixelPoint(), result)
        assertNotEquals(point, result)
    }

    @Test
    fun `should multiply`() {
        val point = PixelPoint(1.px, 10.px)
        val result = point.multiply(10)
        assertEquals(PixelPoint(10.px, 100.px), result)
        assertNotEquals(point, result)
    }

    @Test
    fun `should multiply by coordinate`() {
        val point = PixelPoint(1.px, 10.px)
        val result = point.multiply(10, 2)
        assertEquals(PixelPoint(10.px, 20.px), result)
        assertNotEquals(point, result)
    }
}