package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SteelWallTest {
    private lateinit var eventManager: EventManager
    private lateinit var wall: SteelWall

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        wall = SteelWall(eventManager, mock(), 0, 0)
    }

    @Test
    fun `should be invincible for normal bullets`() {
        val tank = mockTank()
        val bullet = tank.createBullet()
        bullet.direction = Direction.UP
        wall.hit(bullet)
        assertFalse(wall.isDestroyed)
    }

    @Test
    fun `should be destroyed by enhanced bullet`() {
        val tank = mockTank()
        val bullet = tank.createBullet()
        bullet.direction = Direction.UP
        bullet.type = Bullet.Type.ENHANCED
        wall.hit(bullet)
        assertTrue(wall.isDestroyed)
    }
}