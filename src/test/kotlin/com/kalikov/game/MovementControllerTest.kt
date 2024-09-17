package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MovementControllerTest {
    private val updateInterval = 8L

    private lateinit var clock: TestClock
    private lateinit var game: Game
    private lateinit var pauseManager: PauseManager
    private lateinit var mainContainer: SpriteContainer
    private lateinit var overlayContainer: SpriteContainer
    private lateinit var gameField: GameFieldHandle
    private lateinit var movementController: MovementController

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()

        game = mockGame(clock = clock)
        whenever(game.screen.createSurface(px(anyInt()), px(anyInt()))).thenReturn(mock())
        whenever(game.imageManager.getImage(any())).thenReturn(mock())
        whenever(game.imageManager.getImage("wall_brick")).thenReturn(mock())

        pauseManager = mock()
        mainContainer = mock()
        overlayContainer = mock()
        val baseStub = Base(game.eventManager, game.imageManager, t(2).toPixel(), t(2).toPixel())
        val treesStub = Trees(game, px(0), px(0), emptySet())
        val groundStub = Ground(game, px(0), px(0), GroundConfig())
        val wallsStub = Walls(game, px(0), px(0), WallsConfig())
        gameField = mock {
            on { base } doReturn baseStub
            on { trees } doReturn treesStub
            on { ground } doReturn groundStub
            on { walls } doReturn wallsStub
        }
        movementController = MovementController(
            game.eventManager,
            pauseManager,
            PixelRect(px(0), px(0), Globals.CANVAS_WIDTH, Globals.CANVAS_HEIGHT),
            mainContainer,
            overlayContainer,
            gameField,
            clock
        )
    }

    @Test
    fun `bullet should hit base on bullet movement`() {
        val tank = stubPlayerTank(game, pauseManager, px(0), t(2).toPixel())
        val bullet = stubBullet(game, tank, x = tank.right, y = tank.middle - Bullet.SIZE / 2)

        mockMainSprites(listOf(tank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(gameField.base.isHit)
        assertTrue(bullet.isDestroyed)
    }

    @Test
    fun `bullet should hit wall on bullet movement`() {
        val tank = stubPlayerTank(game, pauseManager)
        val bullet = stubBullet(game, tank, x = tank.right, y = tank.middle - Bullet.SIZE / 2)

        gameField.walls.fillBrickTile(t(2), t(0))
        gameField.walls.fillBrickTile(t(2), t(1))
        gameField.walls.fillBrickTile(t(2), t(2))

        mockMainSprites(listOf(tank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertEquals(
            setOf(
                BrickTile(t(2), t(0), 0b0110),
                BrickTile(t(2), t(1), 0b0110),
                BrickTile(t(2), t(2), 0b1111),
            ),
            gameField.walls.config.bricks
        )
        assertTrue(bullet.isDestroyed)
    }

    @Test
    fun `bullet should hit enemy tank on bullet movement`() {
        val tank = stubPlayerTank(game, pauseManager)
        val enemyTank = stubEnemyTank(game, pauseManager, t(2).toPixel(), px(0))

        val bullet = stubBullet(game, tank, x = tank.right, y = tank.middle - Bullet.SIZE / 2)

        mockMainSprites(listOf(enemyTank, tank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(bullet.isDestroyed)
        assertTrue(enemyTank.isDestroyed)
    }

    @Test
    fun `bullet should hit only one enemy tank on bullet movement`() {
        val tank = stubPlayerTank(game, pauseManager)

        val enemyTank1 = stubEnemyTank(game, pauseManager, t(2).toPixel(), px(0))
        val enemyTank2 = stubEnemyTank(game, pauseManager, t(2).toPixel(), px(0))
        val enemyTank3 = stubEnemyTank(game, pauseManager, t(2).toPixel(), px(0))

        val bullet = stubBullet(game, tank, x = tank.right, y = tank.middle - Bullet.SIZE / 2)

        mockMainSprites(listOf(enemyTank1, enemyTank2, enemyTank3, tank, bullet))

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
        val tank1 = stubEnemyTank(game, pauseManager)
        val tank2 = stubEnemyTank(game, pauseManager, t(2).toPixel(), px(0))

        val bullet = stubBullet(game, tank1, x = tank1.right, y = tank1.middle - Bullet.SIZE / 2)

        mockMainSprites(listOf(tank1, tank2, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertFalse(tank2.isDestroyed)
        assertFalse(tank2.isHit)
        assertFalse(bullet.isDestroyed)
    }

    @Test
    fun `bullet should not hit invincible tank on bullet movement`() {
        val enemyTank = stubEnemyTank(game, pauseManager)

        val playerTank = stubPlayerTank(game, pauseManager, t(2).toPixel(), px(0))
        playerTank.state = TankStateInvincible(game, playerTank)

        val bullet = stubBullet(game, enemyTank, x = enemyTank.right, y = enemyTank.middle - Bullet.SIZE / 2)

        mockMainSprites(listOf(enemyTank, playerTank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(bullet.isDestroyed)
        assertFalse(bullet.shouldExplode)
        assertFalse(playerTank.isDestroyed)
    }

    @Test
    fun `bullet should go through appearing tank on bullet movement`() {
        val enemyTank = stubEnemyTank(game, pauseManager)

        val playerTank = stubPlayerTank(game, pauseManager, t(2).toPixel(), px(0))
        playerTank.state = TankStateAppearing(game, playerTank)

        val bullet = stubBullet(game, enemyTank, x = enemyTank.right, y = enemyTank.middle - Bullet.SIZE / 2)

        mockMainSprites(listOf(enemyTank, playerTank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertFalse(bullet.isDestroyed)
        assertFalse(playerTank.isDestroyed)
    }

    @Test
    fun `bullet should hit player tank on bullet movement`() {
        val enemyTank = stubEnemyTank(game, pauseManager)

        val playerTank = stubPlayerTank(game, pauseManager, t(2).toPixel(), px(0))

        val bullet = stubBullet(game, enemyTank, x = enemyTank.right, y = enemyTank.middle - Bullet.SIZE / 2)

        mockMainSprites(listOf(playerTank, enemyTank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(bullet.isDestroyed)
        assertTrue(playerTank.isDestroyed)
    }

    @Test
    fun `enemy bullet should go through enemy bullet`() {
        val tank = stubEnemyTank(game, pauseManager)

        val bullet = stubBullet(game, tank, x = tank.right, y = tank.middle - Bullet.SIZE / 2)

        val otherTank = stubEnemyTank(game, pauseManager, t(2).toPixel() + Bullet.SIZE, px(0))

        val otherBullet = stubBullet(game, otherTank, x = bullet.x, y = bullet.y)

        mockMainSprites(listOf(tank, otherTank, bullet, otherBullet))

        movementController.notify(SpriteContainer.Added(bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertFalse(bullet.isDestroyed)
        assertFalse(otherBullet.isDestroyed)
    }

    @Test
    fun `player bullet should hit enemy bullet`() {
        val playerTank = stubPlayerTank(game, pauseManager)
        val playerBullet = stubBullet(game, playerTank, x = playerTank.right, y = playerTank.middle - Bullet.SIZE / 2)

        val enemyTank = stubEnemyTank(game, pauseManager, t(2).toPixel() + Bullet.SIZE, px(0))
        val enemyBullet = stubBullet(game, enemyTank, x = playerBullet.x, y = playerBullet.y)

        mockMainSprites(listOf(playerTank, enemyTank, playerBullet, enemyBullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(playerBullet.isDestroyed)
        assertTrue(playerBullet.isDestroyed)
        assertFalse(playerTank.isDestroyed)
        assertFalse(enemyTank.isDestroyed)
    }

    @Test
    fun `player bullet should hit enemy bullet on appearance`() {
        val playerTank = stubPlayerTank(game, pauseManager)
        val playerBullet = stubBullet(game, playerTank, x = playerTank.right, y = playerTank.middle - Bullet.SIZE / 2)

        val enemyTank = stubEnemyTank(game, pauseManager, t(2).toPixel() + Bullet.SIZE, px(0))
        val enemyBullet = stubBullet(game, enemyTank, x = playerBullet.x, y = playerBullet.y)

        mockMainSprites(listOf(playerTank, enemyTank, playerBullet, enemyBullet))

        movementController.notify(SpriteContainer.Added(playerBullet))

        assertTrue(playerBullet.isDestroyed)
        assertTrue(playerBullet.isDestroyed)
        assertFalse(playerTank.isDestroyed)
        assertFalse(enemyTank.isDestroyed)
    }

    @Test
    fun `should move normal bullet`() {
        val tank = stubPlayerTank(game, pauseManager)

        val bullet = stubBullet(game, tank, x = tank.right, y = tank.middle - Bullet.SIZE / 2)
        val x = bullet.x

        mockMainSprites(listOf(tank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertEquals(x + 1, bullet.x)
    }

    @Test
    fun `should move fast bullet`() {
        val tank = stubPlayerTank(game, pauseManager)
        tank.bulletSpeed = Bullet.Speed.FAST

        val bullet = stubBullet(game, tank, x = tank.right, y = tank.middle - Bullet.SIZE / 2)
        val x = bullet.x

        mockMainSprites(listOf(tank, bullet))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertEquals(x + 2, bullet.x)
    }

    @Test
    fun `should not move bullet when paused`() {
        whenever(pauseManager.isPaused).thenReturn(true)

        val tank = stubPlayerTank(game, pauseManager)
        tank.bulletSpeed = Bullet.Speed.FAST

        val bullet = stubBullet(game, tank, x = tank.right, y = tank.middle - Bullet.SIZE / 2)
        val x = bullet.x
        val y = bullet.y

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertEquals(x, bullet.x)
        assertEquals(y, bullet.y)
    }

    @Test
    fun `tank should collide wall on tank movement left`() {
        val tank = stubPlayerTank(game, pauseManager, t(1).toPixel() - 2, px(0))
        tank.moveFrequency = 1
        tank.direction = Direction.LEFT
        tank.isIdle = false

        gameField.walls.fillBrickTile(t(0), t(0))

        mockMainSprites(listOf(tank))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertEquals(PixelPoint(t(1).toPixel() - 2, px(0)), PixelPoint(tank.x, tank.y))
    }

    @Test
    fun `tank should pick power up on tank movement`() {
        val tank = stubPlayerTank(game, pauseManager)
        tank.isIdle = false
        val powerUp = stubPowerUp(game)

        mockMainSprites(listOf(tank))
        mockOverlaySprites(listOf(powerUp))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertTrue(powerUp.isDestroyed)
        verify(game.eventManager).fireEvent(PowerUp.Pick(powerUp, tank))
    }

    @Test
    fun `tanks should overlap when one is out of bounds`() {
        val tank1 = stubPlayerTank(game, pauseManager, px(0), t(-1).toPixel())
        tank1.direction = Direction.DOWN
        tank1.isIdle = false
        val tank2 = stubPlayerTank(game, pauseManager)

        mockMainSprites(listOf(tank1, tank2))

        movementController.update()
        clock.tick(updateInterval)
        movementController.update()

        assertEquals(px(0), tank1.x)
        assertEquals(t(-1).toPixel(), tank1.y)
        assertEquals(px(0), tank2.x)
        assertEquals(px(0), tank2.y)
    }

    private fun mockMainSprites(sprites: List<Sprite>) {
        mockSprites(mainContainer, sprites)
    }

    private fun mockOverlaySprites(sprites: List<Sprite>) {
        mockSprites(overlayContainer, sprites)
    }

    private fun mockSprites(spriteContainer: SpriteContainer, sprites: List<Sprite>) {
        whenever(spriteContainer.forEach(any())).thenAnswer {
            sprites.forEach(it.getArgument(0))
        }
        whenever(spriteContainer.iterateWhile(any())).thenAnswer {
            val action: (Sprite) -> Boolean = it.getArgument(0)
            for (sprite in sprites) {
                if (!action(sprite)) {
                    return@thenAnswer false
                }
            }
            true
        }
    }
}