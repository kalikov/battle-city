//package com.kalikov.game
//
//import com.kalikov.engine.px
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.argumentCaptor
//import org.mockito.kotlin.isA
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.verify
//import kotlin.test.assertEquals
//import kotlin.test.assertSame
//
//class TankExplosionFactoryTest {
//    private lateinit var game: BattleCityGame
//    private lateinit var spriteContainer: SpriteContainer
//    private lateinit var factory: TankExplosionFactory
//
//    @BeforeEach
//    fun beforeEach() {
//        game = mockGame()
//        spriteContainer = mock()
//        factory = TankExplosionFactory(game, mock(), spriteContainer)
//    }
//
//    @Test
//    fun `should subscribe`() {
//        verify(game.eventManager).addSubscriber(factory, arrayOf(Tank.Destroyed::class))
//    }
//
//    @Test
//    fun `should unsubscribe`() {
//        factory.dispose()
//        verify(game.eventManager).removeSubscriber(factory, arrayOf(Tank.Destroyed::class))
//    }
//
//    @Test
//    fun `should create explosion when tank is destroyed`() {
//        val tank = stubPlayerTank(game)
//        factory.notify(Tank.Destroyed(tank))
//
//        verify(spriteContainer).addSprite(isA<TankExplosion>())
//    }
//
//    @Test
//    fun `should correctly place created explosion`() {
//        val tank = stubPlayerTank(game, x = 5.px, y = 6.px)
//        factory.notify(Tank.Destroyed(tank))
//
//        val captor = argumentCaptor<TankExplosion>()
//        verify(spriteContainer).addSprite(captor.capture())
//
//        val explosion = captor.firstValue
//        assertEquals(
//            PixelRect(
//                5.px - 1.tiles.toPixel(),
//                6.px - 1.tiles.toPixel(),
//                4.tiles.toPixel(),
//                4.tiles.toPixel(),
//            ),
//            explosion.bounds
//        )
//        assertSame(tank, explosion.tank)
//    }
//}