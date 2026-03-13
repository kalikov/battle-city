//package com.kalikov.game
//
//import com.kalikov.engine.EventManager
//import com.kalikov.engine.px
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.argumentCaptor
//import org.mockito.kotlin.doReturn
//import org.mockito.kotlin.isA
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.verify
//import kotlin.test.assertEquals
//
//class BaseExplosionFactoryTest {
//    private lateinit var eventManager: EventManager
//    private lateinit var spriteContainer: SpriteContainer
//
//    private lateinit var factory: BaseExplosionFactory
//
//    @BeforeEach
//    fun beforeEach() {
//        val game = mockGame()
//        eventManager = game.eventManager
//        spriteContainer = mock()
//        factory = BaseExplosionFactory(game, mock(), spriteContainer)
//    }
//
//    @Test
//    fun `should subscribe`() {
//        verify(eventManager).addSubscriber(factory, arrayOf(Base.Hit::class))
//    }
//
//    @Test
//    fun `should create explosion on base hit`() {
//        val base = mock<BaseHandle>()
//
//        factory.notify(Base.Hit(base))
//
//        verify(spriteContainer).addSprite(isA<BaseExplosion>())
//    }
//
//    @Test
//    fun `should position explosion at the base center`() {
//        val base = mock<BaseHandle> {
//            on { x } doReturn 10.px
//            on { y } doReturn 100.px
//        }
//
//        factory.notify(Base.Hit(base))
//
//        val captor = argumentCaptor<BaseExplosion>()
//        verify(spriteContainer).addSprite(captor.capture())
//
//        val explosion = captor.firstValue
//        assertEquals(
//            PixelRect(
//                base.x + -explosion.width / 2 + Base.SIZE / 2,
//                base.y + -explosion.height / 2 + Base.SIZE / 2,
//                explosion.width,
//                explosion.height
//            ),
//            explosion.bounds
//        )
//    }
//}