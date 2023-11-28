package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TankColorTest {
    private lateinit var clock: TestClock
    private lateinit var color: TankColor

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        color = TankColor(clock)
        color.colors = arrayOf(intArrayOf(0, 1), intArrayOf(0, 2), intArrayOf(1, 2), intArrayOf(0))
    }

    @Test
    fun `should be flashing with no hits colors`() {
        color.update()
        assertEquals(0, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(1, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(0, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(1, color.getColor())
    }

    @Test
    fun `should be flashing with one hit colors`() {
        color.update()
        color.hit()
        assertEquals(0, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(2, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(0, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(2, color.getColor())
    }

    @Test
    fun `should be flashing with two hits colors`() {
        color.update()
        color.hit()
        color.hit()
        assertEquals(1, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(2, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(1, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(2, color.getColor())
    }

    @Test
    fun `should be flashing with three hits colors`() {
        color.update()
        color.hit()
        color.hit()
        color.hit()
        assertEquals(0, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(0, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(0, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(0, color.getColor())
    }

    @Test
    fun `should be flashing with four hits colors`() {
        color.update()
        color.hit()
        color.hit()
        color.hit()
        color.hit()
        assertEquals(0, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(0, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(0, color.getColor())

        clock.tick(TankColor.FLASHING_INTERVAL)
        color.update()
        assertEquals(0, color.getColor())
    }
}