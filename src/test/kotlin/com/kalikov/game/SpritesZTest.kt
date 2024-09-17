package com.kalikov.game

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SpritesZTest {
    private lateinit var tank: Tank
    private lateinit var explosion: Explosion
    private lateinit var bullet: Bullet
    private lateinit var points: Points
    private lateinit var powerUp: PowerUp
    private lateinit var cursor: Cursor

    @BeforeEach
    fun beforeEach() {
        tank = stubPlayerTank()
        explosion = stubTankExplosion(tank = tank)
        bullet = stubBullet(tank = tank)
        points = stubPoints()
        powerUp = stubPowerUp()
        cursor = stubCursor()
    }

    @Test
    fun `bullet is above tank`() {
        assertTrue(bullet.z > tank.z)
    }

    @Test
    fun `tank is below explosion`() {
        assertTrue(tank.z < explosion.z)
    }

    @Test
    fun `power-up is above explosion`() {
        assertTrue(powerUp.z > explosion.z)
    }

    @Test
    fun `points are above explosion`() {
        assertTrue(points.z > explosion.z)
    }

    @Test
    fun `explosion is above tank`() {
        assertTrue(explosion.z > tank.z)
    }

    @Test
    fun `explosion is above bullet`() {
        assertTrue(explosion.z > bullet.z)
    }

    @Test
    fun `cursor is above everything`() {
        assertTrue(cursor.z > tank.z)
        assertTrue(cursor.z > explosion.z)
        assertTrue(cursor.z > bullet.z)
        assertTrue(cursor.z > points.z)
        assertTrue(cursor.z > powerUp.z)
    }
}