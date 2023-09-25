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
        color.colors = arrayOf(0 to 1, 0 to 2, 1 to 2, 0 to 0)
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