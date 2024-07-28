package com.kalikov.game

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SpritesZTest {
    private lateinit var tank: Tank
    private lateinit var explosion: Explosion
    private lateinit var bullet: Bullet
    private lateinit var base: Base
    private lateinit var points: Points
    private lateinit var water: Water
    private lateinit var trees: Trees
    private lateinit var ice: Ice
    private lateinit var brickWall: BrickWall
    private lateinit var steelWall: SteelWall
    private lateinit var powerUp: PowerUp
    private lateinit var cursor: Cursor

    @BeforeEach
    fun beforeEach() {
        tank = mockPlayerTank()
        explosion = mockTankExplosion(tank = tank)
        bullet = tank.createBullet()
        base = mockBase()
        points = mockPoints()
        water = mockWater()
        trees = mockTrees()
        ice = mockIce()
        brickWall = mockBrickWall()
        steelWall = mockSteelWall()
        powerUp = mockPowerUp()
        cursor = mockCursor()
    }

    @Test
    fun `walls should have same z value`() {
        assertEquals(brickWall.z, steelWall.z)
    }

    @Test
    fun `trees are above walls`() {
        assertTrue(trees.z > brickWall.z)
        assertTrue(trees.z > steelWall.z)
    }

    @Test
    fun `water is below tank`() {
        assertTrue(water.z < tank.z)
    }

    @Test
    fun `tank is above walls`() {
        assertTrue(tank.z > brickWall.z)
        assertTrue(tank.z > steelWall.z)
    }

    @Test
    fun `tank is above water`() {
        assertTrue(tank.z > water.z)
    }

    @Test
    fun `base and walls have the same z value`() {
        assertEquals(base.z, brickWall.z)
        assertEquals(base.z, steelWall.z)
    }

    @Test
    fun `tank is above base`() {
        assertTrue(tank.z > base.z)
    }

    @Test
    fun `tank is below trees`() {
        assertTrue(tank.z < trees.z)
    }

    @Test
    fun `bullet is below trees`() {
        assertTrue(bullet.z < trees.z)
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
    fun `base is below explosion`() {
        assertTrue(base.z < explosion.z)
    }

    @Test
    fun `base is below trees`() {
        assertTrue(base.z < trees.z)
    }

    @Test
    fun `base is above water`() {
        assertTrue(base.z > water.z)
    }

    @Test
    fun `power-up is above trees`() {
        assertTrue(powerUp.z > trees.z)
    }

    @Test
    fun `points are above trees`() {
        assertTrue(points.z > trees.z)
    }

    @Test
    fun `water and ice have the same z value`() {
        assertEquals(water.z, ice.z)
    }

    @Test
    fun `explosion is above tree`() {
        assertTrue(explosion.z > trees.z)
    }

    @Test
    fun `cursor is above everything`() {
        assertTrue(cursor.z > tank.z)
        assertTrue(cursor.z > explosion.z)
        assertTrue(cursor.z > bullet.z)
        assertTrue(cursor.z > base.z)
        assertTrue(cursor.z > points.z)
        assertTrue(cursor.z > water.z)
        assertTrue(cursor.z > trees.z)
        assertTrue(cursor.z > brickWall.z)
        assertTrue(cursor.z > steelWall.z)
        assertTrue(cursor.z > powerUp.z)
    }
}