package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.random.Random

class AITankControllerTest {
    private lateinit var clock: TestClock
    private lateinit var tank: AITankHandle
    private lateinit var random: Random

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        tank = mock()
        whenever(tank.y).thenReturn(-Tank.SIZE)
        whenever(tank.moveFrequency).thenReturn(8)
        whenever(tank.hitRect).thenReturn(PixelRect(px(0), -Tank.SIZE, Tank.SIZE, Tank.SIZE))
        random = mock()
    }

    @Test
    fun `should set tank's speed`() {
        createController()
        verify(tank).isIdle = false
    }

    @Test
    fun `should shoot on first update`() {
        val controller = createController(
            AITankControllerParams(
                clock = clock,
                shootInterval = 1,
                shootProbability = 1.0
            )
        )

        whenever(random.nextDouble()).thenReturn(0.7)

        controller.update()
        clock.tick(1)
        controller.update()

        verify(tank).shoot()
    }

    @Test
    fun `should shoot on third update`() {
        val controller = createController(
            AITankControllerParams(
                clock = clock,
                shootInterval = 3,
                shootProbability = 1.0
            )
        )

        whenever(random.nextDouble()).thenReturn(0.7)

        controller.update()
        clock.tick(1)
        controller.update()

        verify(tank, never()).shoot()
        clock.tick(1)
        controller.update()
        verify(tank, never()).shoot()

        clock.tick(1)
        controller.update()
        verify(tank).shoot()

        clearInvocations(tank)

        clock.tick(1)
        controller.update()
        verify(tank, never()).shoot()

        clock.tick(1)
        controller.update()
        verify(tank, never()).shoot()

        clock.tick(1)
        controller.update()
        verify(tank).shoot()
    }

    @Test
    fun `should not shoot on first update when probability threshold is exceeded`() {
        val controller = createController(
            AITankControllerParams(
                clock = clock,
                shootInterval = 1,
                shootProbability = 0.5
            )
        )

        whenever(random.nextDouble()).thenReturn(0.6)

        controller.update()
        clock.tick(1)
        controller.update()

        verify(tank, never()).shoot()
    }

    @Test
    fun `should not shoot on first update when probability threshold is reached`() {
        val controller = createController(
            AITankControllerParams(
                clock = clock,
                shootInterval = 1,
                shootProbability = 0.5
            )
        )

        whenever(random.nextDouble()).thenReturn(0.5)

        controller.update()
        clock.tick(1)
        controller.update()

        verify(tank, never()).shoot()
    }

    @Test
    fun `should shoot on first update when probability threshold is not exceeded`() {
        val controller = createController(
            AITankControllerParams(
                clock = clock,
                shootInterval = 1,
                shootProbability = 0.5
            )
        )

        whenever(random.nextDouble()).thenReturn(0.4)

        controller.update()
        clock.tick(1)
        controller.update()

        verify(tank).shoot()
    }

    @Test
    fun `should update direction on first update`() {
        val controller = createController(
            AITankControllerParams(
                clock = clock,
                directionUpdateInterval = 1,
                directionUpdateProbability = 1.0
            )
        )

        whenever(random.nextDouble()).thenReturn(0.7)

        controller.update()
        clock.tick(1)
        controller.update()

        verify(tank).direction = Direction.DOWN
        verify(random, never()).nextInt(any())
    }

    @Test
    fun `should update direction on third update`() {
        val controller = createController(
            AITankControllerParams(
                clock = clock,
                directionUpdateInterval = 3,
                directionUpdateProbability = 1.0
            )
        )

        whenever(random.nextDouble()).thenReturn(0.7)

        controller.update()
        clock.tick(1)
        controller.update()
        verify(tank, never()).direction = any()

        clock.tick(1)
        controller.update()
        verify(tank, never()).direction = any()

        clock.tick(1)
        controller.update()
        verify(tank).direction = Direction.DOWN

        clearInvocations(tank)

        clock.tick(1)
        controller.update()
        verify(tank, never()).direction = any()

        clock.tick(1)
        controller.update()
        verify(tank, never()).direction = any()

        clock.tick(1)
        controller.update()
        verify(tank).direction = Direction.DOWN

        verify(random, never()).nextInt(any())
    }

    @Test
    fun `should not update direction when probability threshold is exceeded`() {
        val controller = createController(
            AITankControllerParams(
                clock = clock,
                directionUpdateInterval = 1,
                directionUpdateProbability = 0.5
            )
        )

        whenever(random.nextDouble()).thenReturn(0.6)

        controller.update()
        clock.tick(1)
        controller.update()

        verify(tank, never()).direction = any()
        verify(random, never()).nextInt(any())
    }

    @Test
    fun `should not update direction when probability threshold is reached`() {
        val controller = createController(
            AITankControllerParams(
                clock = clock,
                directionUpdateInterval = 1,
                directionUpdateProbability = 0.5
            )
        )

        whenever(random.nextDouble()).thenReturn(0.5)

        controller.update()
        clock.tick(1)
        controller.update()

        verify(tank, never()).direction = any()
        verify(random, never()).nextInt(any())
    }

    @Test
    fun `should not update direction when probability threshold is not exceeded`() {
        val controller = createController(
            AITankControllerParams(
                clock = clock,
                directionUpdateInterval = 1,
                directionUpdateProbability = 0.5,
                directionRetreatProbability = 0.5
            )
        )

        whenever(random.nextDouble()).thenReturn(0.4)
        whenever(random.nextInt(3)).thenReturn(1)

        controller.update()
        clock.tick(1)
        controller.update()

        verify(tank).direction = Direction.LEFT
    }

    private fun createController(params: AITankControllerParams = AITankControllerParams()): AITankController {
        return AITankController(mock(), tank, PixelPoint(), emptySet(), random, params)
    }
}