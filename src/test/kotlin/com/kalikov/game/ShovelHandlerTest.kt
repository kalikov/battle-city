package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify

class ShovelHandlerTest {
    private lateinit var eventManager: EventManager
    private lateinit var clock: TestClock
    private lateinit var baseWallBuilder: ShovelWallBuilder
    private lateinit var handler: ShovelHandler

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        baseWallBuilder = mock()
        clock = TestClock()
        handler = ShovelHandler(eventManager, mock(), baseWallBuilder, clock)
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(handler, setOf(PowerUpHandler.ShovelStart::class))
    }

    @Test
    fun `should unsubscribe`() {
        handler.dispose()
        verify(eventManager).removeSubscriber(handler, setOf(PowerUpHandler.ShovelStart::class))
    }

    @Test
    fun `should build walls on shovel`() {
        handler.notify(PowerUpHandler.ShovelStart)

        verify(baseWallBuilder).destroyWall()
        verify(baseWallBuilder).buildWall(isA<SteelWallFactory>())
    }

    @Test
    fun `should end on timer end`() {
        handler.notify(PowerUpHandler.ShovelStart)
        reset(baseWallBuilder)

        clock.tick(ShovelHandler.SHOVEL_DURATION / 2)
        handler.update()
        verify(baseWallBuilder, never()).buildWall(isA<BrickWallFactory>())

        clock.tick(ShovelHandler.SHOVEL_DURATION / 2)
        handler.update()
        verify(baseWallBuilder).buildWall(isA<BrickWallFactory>())
    }

    @Test
    fun `should not update when paused`() {
        eventManager = ConcurrentEventManager()
        handler = ShovelHandler(eventManager, mock(), baseWallBuilder, clock)
        handler.notify(PowerUpHandler.ShovelStart)
        reset(baseWallBuilder)

        eventManager.fireEvent(PauseManager.Start)

        clock.tick(10 * ShovelHandler.SHOVEL_DURATION)
        handler.update()
        verify(baseWallBuilder, never()).buildWall(isA<BrickWallFactory>())

        eventManager.fireEvent(PauseManager.End)

        clock.tick(ShovelHandler.SHOVEL_DURATION)
        handler.update()
        verify(baseWallBuilder).buildWall(isA<BrickWallFactory>())
    }
}