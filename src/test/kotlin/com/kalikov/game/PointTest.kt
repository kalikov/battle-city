package com.kalikov.game

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PointTest {
    @Test
    fun `should create using no-arg constructor`() {
        val point = Point()
        assertEquals(0, point.x)
        assertEquals(0, point.y)
        assertEquals(Point(0, 0), point)
    }

    @Test
    fun `should create using coordinates as arguments`() {
        val point = Point(1, 2)
        assertEquals(1, point.x)
        assertEquals(2, point.y)
    }

    @Test
    fun `should translate`() {
        val point = Point(10, 100)
        val result = point.translate(-10, -100)
        assertEquals(Point(0, 0), result)
        assertNotEquals(point, result)
    }

    @Test
    fun `should multiply`() {
        val point = Point(1, 10)
        val result = point.multiply(10)
        assertEquals(Point(10, 100), result)
        assertNotEquals(point, result)
    }

    @Test
    fun `should multiply by coordinate`() {
        val point = Point(1, 10)
        val result = point.multiply(10, 2)
        assertEquals(Point(10, 20), result)
        assertNotEquals(point, result)
    }
}