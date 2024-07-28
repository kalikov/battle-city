package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MovementControllerTest {
    private val updateInterval = 8L

    private lateinit var clock: TestClock
    private lateinit var eventManager: EventManager
    private lateinit var pauseManager: PauseManager
    private lateinit var spriteContainer: SpriteContainer
    private lateinit var movementController: MovementController

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        eventManager = mock()
        pauseManager = mock()
        spriteContainer = mock()
        movementController = MovementController(
            eventManager,
            pauseManager,
            Rect(0, 0, Globals.CANVAS_WIDTH, Globals.CANVAS_HEIGHT),
            spriteContainer,
            clock
        )
    }

    @Test
    fun `bullet should hit base on bullet movement`() {
        val tank = mockPlayerTank(eventManager)
        tank.direction = Direction.RIGHT
        val bullet = tank.createBullet()

        val base = Base(eventManager, mock(), Globals.UNIT_SIZE, 0)

        whenever(spriteContainer.sprites).thenReturn(listOf(base, tank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(base.isHit)
        assertTrue(bullet.isDestroyed)
    }

    @Test
    fun `bullet should hit wall on bullet movement`() {
        val tank = mockPlayerTank(eventManager)
        tank.direction = Direction.RIGHT
        val bullet = tank.createBullet()

        val wall = mockBrickWall(eventManager, x = Globals.UNIT_SIZE, y = 0)

        whenever(spriteContainer.sprites).thenReturn(listOf(wall, tank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(wall.isHitLeft)
        assertTrue(bullet.isDestroyed)
    }

    @Test
    fun `bullet should hit enemy tank on bullet movement`() {
        val tank = mockPlayerTank(eventManager)
        tank.direction = Direction.RIGHT

        val enemyTank = mockEnemyTank(eventManager, x = Globals.UNIT_SIZE, y = 0)

        val bullet = tank.createBullet()

        whenever(spriteContainer.sprites).thenReturn(listOf(enemyTank, tank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(bullet.isDestroyed)
        assertTrue(enemyTank.isDestroyed)
    }

    @Test
    fun `bullet should hit only one enemy tank on bullet movement`() {
        val tank = mockPlayerTank(eventManager)
        tank.direction = Direction.RIGHT

        val enemyTank1 = mockEnemyTank(eventManager, x = Globals.UNIT_SIZE, y = 0)
        val enemyTank2 = mockEnemyTank(eventManager, x = Globals.UNIT_SIZE, y = 0)
        val enemyTank3 = mockEnemyTank(eventManager, x = Globals.UNIT_SIZE, y = 0)

        val bullet = tank.createBullet()

        whenever(spriteContainer.sprites).thenReturn(listOf(enemyTank1, enemyTank2, enemyTank3, tank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(bullet.isDestroyed)
        assertTrue(enemyTank1.isDestroyed)
        assertFalse(enemyTank2.isDestroyed)
        assertFalse(enemyTank3.isDestroyed)
    }

    @Test
    fun `enemy bullet should go through enemy tank on bullet movement`() {
        val tank1 = mockEnemyTank(eventManager)
        tank1.direction = Direction.RIGHT

        val tank2 = mockEnemyTank(eventManager, x = Globals.UNIT_SIZE, y = 0)

        val bullet = tank1.createBullet()

        whenever(spriteContainer.sprites).thenReturn(listOf(tank1, tank2, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertFalse(tank2.isDestroyed)
        assertFalse(tank2.isHit)
        assertFalse(bullet.isDestroyed)
    }

    @Test
    fun `bullet should not hit invincible tank on bullet movement`() {
        val enemyTank = mockEnemyTank(eventManager)
        enemyTank.direction = Direction.RIGHT

        val playerTank = mockPlayerTank(eventManager, x = Globals.UNIT_SIZE, y = 0)
        playerTank.state = TankStateInvincible(eventManager, mock(), playerTank)

        val bullet = enemyTank.createBullet()

        whenever(spriteContainer.sprites).thenReturn(listOf(enemyTank, playerTank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(bullet.isDestroyed)
        assertFalse(bullet.shouldExplode)
        assertFalse(playerTank.isDestroyed)
    }

    @Test
    fun `bullet should go through appearing tank on bullet movement`() {
        val enemyTank = mockEnemyTank(eventManager)
        enemyTank.direction = Direction.RIGHT

        val playerTank = mockPlayerTank(eventManager, x = Globals.UNIT_SIZE, y = 0)
        playerTank.state = TankStateAppearing(eventManager, mock(), playerTank)

        val bullet = enemyTank.createBullet()

        whenever(spriteContainer.sprites).thenReturn(listOf(enemyTank, playerTank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertFalse(bullet.isDestroyed)
        assertFalse(playerTank.isDestroyed)
    }

    @Test
    fun `bullet should hit player tank on bullet movement`() {
        val enemyTank = mockEnemyTank(eventManager)
        enemyTank.direction = Direction.RIGHT

        val playerTank = mockPlayerTank(eventManager, x = Globals.UNIT_SIZE, y = 0)

        val bullet = enemyTank.createBullet()

        whenever(spriteContainer.sprites).thenReturn(listOf(playerTank, enemyTank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(bullet.isDestroyed)
        assertTrue(playerTank.isDestroyed)
    }

    @Test
    fun `enemy bullet should go through enemy bullet`() {
        val tank = mockEnemyTank(eventManager)
        tank.direction = Direction.RIGHT

        val bullet = tank.createBullet()

        val otherTank = mockEnemyTank(eventManager, x = Globals.UNIT_SIZE + Bullet.SIZE, y = 0)
        otherTank.direction = Direction.LEFT

        val otherBullet = otherTank.createBullet()

        whenever(spriteContainer.sprites).thenReturn(listOf(tank, otherTank, bullet, otherBullet))

        movementController.notify(SpriteContainer.Added(bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertFalse(bullet.isDestroyed)
        assertFalse(otherBullet.isDestroyed)
    }

    @Test
    fun `player bullet should hit enemy bullet`() {
        val player = mockPlayerTank(eventManager)
        player.direction = Direction.RIGHT

        val playerBullet = player.createBullet()

        val tank = mockEnemyTank(eventManager, x = Globals.UNIT_SIZE + Bullet.SIZE, y = 0)
        tank.direction = Direction.LEFT

        val bullet = tank.createBullet()

        whenever(spriteContainer.sprites).thenReturn(listOf(player, tank, playerBullet, bullet))

        movementController.notify(SpriteContainer.Added(bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(playerBullet.isDestroyed)
        assertTrue(bullet.isDestroyed)
        assertFalse(player.isDestroyed)
        assertFalse(tank.isDestroyed)
    }

    @Test
    fun `should move normal bullet`() {
        val tank = mockPlayerTank(eventManager)
        tank.direction = Direction.RIGHT
        tank.bulletSpeed = Bullet.Speed.NORMAL

        val bullet = tank.createBullet()
        val x = bullet.x

        whenever(spriteContainer.sprites).thenReturn(listOf(tank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertEquals(x + 1, bullet.x)
    }

    @Test
    fun `should move fast bullet`() {
        val tank = mockPlayerTank(eventManager)
        tank.direction = Direction.RIGHT
        tank.bulletSpeed = Bullet.Speed.FAST

        val bullet = tank.createBullet()
        val x = bullet.x

        whenever(spriteContainer.sprites).thenReturn(listOf(tank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertEquals(x + 2, bullet.x)
    }

    @Test
    fun `should not move bullet when paused`() {
        whenever(pauseManager.isPaused).thenReturn(true)

        val tank = mockPlayerTank(eventManager)
        tank.direction = Direction.RIGHT
        tank.bulletSpeed = Bullet.Speed.FAST

        val bullet = tank.createBullet()
        val x = bullet.x
        val y = bullet.y

        bullet.direction = Direction.DOWN

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertEquals(x, bullet.x)
        assertEquals(y, bullet.y)
    }

    @Test
    fun `tank should collide wall on tank movement left`() {
        val tank = mockPlayerTank(eventManager, x = Globals.TILE_SIZE - 2, y = 0)
        tank.moveFrequency = 1
        tank.direction = Direction.LEFT
        tank.isIdle = false
        val wall = mockBrickWall(eventManager)

        whenever(spriteContainer.sprites).thenReturn(listOf(wall, tank))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertEquals(Point(Globals.TILE_SIZE - 2, 0), Point(tank.x, tank.y))
    }

    @Test
    fun `tank should pick power up on tank movement`() {
        val tank = mockPlayerTank(eventManager)
        tank.isIdle = false
        val powerUp = mockPowerUp(eventManager)

        whenever(spriteContainer.sprites).thenReturn(listOf(tank, powerUp))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(powerUp.isDestroyed)
        verify(eventManager).fireEvent(PowerUp.Pick(powerUp, tank))
    }

    @Test
    fun `tanks should overlap when one is out of bounds`() {
        val tank1 = mockPlayerTank(eventManager, x = 0, y = -8)
        tank1.direction = Direction.DOWN
        tank1.isIdle = false
        val tank2 = mockPlayerTank(eventManager)

        whenever(spriteContainer.sprites).thenReturn(listOf(tank1, tank2))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertEquals(Point(0, -8), tank1.position)
        assertEquals(Point(0, 0), tank2.position)
    }
}