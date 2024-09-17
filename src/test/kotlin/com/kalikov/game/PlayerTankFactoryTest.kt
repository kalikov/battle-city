package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame

class PlayerTankFactoryTest {
    private lateinit var eventManager: EventManager
    private lateinit var game: Game
    private lateinit var spriteContainer: SpriteContainer
    private lateinit var factory: PlayerTankFactory

    @BeforeEach
    fun beforeEach() {
        game = mockGame()
        eventManager = game.eventManager
        spriteContainer = mock()
        factory = PlayerTankFactory(
            game,
            mock(),
            spriteContainer,
            PixelPoint(px(10), px(100)),
            Player(eventManager)
        )
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(factory, setOf(TankExplosion.Destroyed::class, Player.OutOfLives::class))
    }

    @Test
    fun `should unsubscribe`() {
        factory.dispose()
        verify(eventManager).removeSubscriber(factory, setOf(TankExplosion.Destroyed::class, Player.OutOfLives::class))
    }

    @Test
    fun `should create tank right away`() {
        factory.init(0)
        val tank = factory.playerTank
        assertNotNull(tank)
        assertEquals(px(10), tank.x)
        assertEquals(px(100), tank.y)
        assertIs<TankStateAppearing>(tank.state)
        verify(eventManager).fireEvent(PlayerTankFactory.PlayerTankCreated(tank))
    }

    @Test
    fun `should create new tank after explosion`() {
        factory.init(0)
        reset(eventManager)

        val explosion = stubTankExplosion(game, tank = factory.playerTank!!)
        factory.notify(TankExplosion.Destroyed(explosion))

        verify(eventManager).fireEvent(isA<PlayerTankFactory.PlayerTankCreated>())
    }

    @Test
    fun `should add tank to the sprite container`() {
        factory.init(0)
        reset(eventManager)
        val firstTank = factory.playerTank
        assertNotNull(firstTank)

        factory.notify(TankExplosion.Destroyed(stubTankExplosion(game, tank = firstTank)))

        val captor = argumentCaptor<PlayerTankFactory.PlayerTankCreated>()
        verify(eventManager).fireEvent(captor.capture())

        val tank = captor.firstValue.tank
        assertNotSame(firstTank, tank)
        assertSame(tank, factory.playerTank)

        verify(spriteContainer).addSprite(tank)
        assertEquals(px(10), tank.x)
        assertEquals(px(100), tank.y)
        assertIs<TankStateAppearing>(tank.state)
    }

    @Test
    fun `should not create new tank when player is out of lives`() {
        factory.init(0)
        reset(eventManager)

        factory.notify(Player.OutOfLives(factory.player))
        val explosion = stubTankExplosion(game, tank = factory.playerTank!!)
        factory.notify(TankExplosion.Destroyed(explosion))

        verify(eventManager, never()).fireEvent(isA<PlayerTankFactory.PlayerTankCreated>())
        assertNull(factory.playerTank)
    }

    @Test
    fun `should upgrade tank once`() {
        factory.init(1)
        val tank = factory.playerTank
        assertNotNull(tank)
        assertEquals(1, tank.upgradeLevel)
        assertEquals(Bullet.Speed.FAST, tank.bulletSpeed)
        assertEquals(1, tank.bulletsLimit)
    }

    @Test
    fun `should upgrade tank twice`() {
        factory.init(2)
        val tank = factory.playerTank
        assertNotNull(tank)
        assertEquals(2, tank.upgradeLevel)
        assertEquals(Bullet.Speed.FAST, tank.bulletSpeed)
        assertEquals(Bullet.Type.REGULAR, tank.bulletType)
        assertEquals(2, tank.bulletsLimit)
    }

    @Test
    fun `should upgrade tank three times`() {
        factory.init(3)
        val tank = factory.playerTank
        assertNotNull(tank)
        assertEquals(3, tank.upgradeLevel)
        assertEquals(Bullet.Speed.FAST, tank.bulletSpeed)
        assertEquals(Bullet.Type.ENHANCED, tank.bulletType)
        assertEquals(2, tank.bulletsLimit)
    }
}