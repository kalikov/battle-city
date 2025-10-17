package com.kalikov.game

import com.kalikov.util.TestClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ShovelHandlerTest {
    private lateinit var clock: TestClock
    private lateinit var game: BattleCityGame
    private lateinit var pauseManager: PauseManager
    private lateinit var baseWallBuilder: ShovelWallBuilder
    private lateinit var handler: ShovelHandler

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        game = mockGame(clock = clock)
        pauseManager = mock()
        baseWallBuilder = mock()
        handler = ShovelHandler(game, pauseManager, baseWallBuilder)
    }

    @Test
    fun `should subscribe`() {
        verify(game.eventManager).addSubscriber(handler, arrayOf(PowerUpHandler.ShovelStart::class))
    }

    @Test
    fun `should unsubscribe`() {
        handler.dispose()
        verify(game.eventManager).removeSubscriber(handler, arrayOf(PowerUpHandler.ShovelStart::class))
    }

    @Test
    fun `should build walls on shovel`() {
        handler.notify(PowerUpHandler.ShovelStart)

        verify(baseWallBuilder).buildSteelWall()
    }

    @Test
    fun `should end on timer end`() {
        handler.notify(PowerUpHandler.ShovelStart)
        reset(baseWallBuilder)

        clock.tick(ShovelHandler.SOLID_DURATION / 2)
        handler.update()
        verify(baseWallBuilder, never()).buildBrickWall()

        clock.tick(ShovelHandler.SOLID_DURATION / 2)
        handler.update()
        verify(baseWallBuilder).buildBrickWall()
    }

    @Test
    fun `should not update when paused`() {
        handler.notify(PowerUpHandler.ShovelStart)
        reset(baseWallBuilder)

        whenever(pauseManager.isPaused).thenReturn(true)
        handler.update()

        clock.tick(10 * ShovelHandler.SOLID_DURATION)
        handler.update()
        verify(baseWallBuilder, never()).buildBrickWall()

        whenever(pauseManager.isPaused).thenReturn(false)
        handler.update()

        clock.tick(ShovelHandler.SOLID_DURATION)
        handler.update()
        verify(baseWallBuilder).buildBrickWall()
    }
}