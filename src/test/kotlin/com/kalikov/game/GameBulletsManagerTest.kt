package com.kalikov.game

import com.kalikov.engine.EventManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class GameBulletsManagerTest {
    private lateinit var fonts: TestFonts

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
    }

    @Test
    fun `should subscribe`() {
        val eventManager: EventManager = mock()

        val factory = GameBulletsManager(mockGame(eventManager = eventManager))
        factory.activate()
        verify(eventManager).addSubscriber(factory, arrayOf(Tank.Shoot::class))
    }

    @Test
    fun `should unsubscribe`() {
        val eventManager: EventManager = mock()

        val factory = GameBulletsManager(mockGame(eventManager = eventManager))
        factory.deactivate()
        verify(eventManager).removeSubscriber(factory, arrayOf(Tank.Shoot::class))
    }

    @Test
    fun `should fire event on destroy`() {
//        bullet.destroy()
//        bullet.update()
//        verify(eventManager).fireEvent(Bullet.Exploded(bullet))
    }

    @Test
    fun `should fire event when exploded`() {
//        bullet.hit(true)
//        bullet.update()
//        verify(eventManager).fireEvent(Bullet.Exploded(bullet))
//        verify(bullet.tank, never()).reload()
    }

//    @Test
//    fun `should add bullet to container when tank shoots`() {
//        val spriteContainer: SpriteContainer = mock()
//        val factory = GameBulletsManager(mockGame(), spriteContainer)
//
//        val bullet = stubBullet(tank = stubEnemyTank())
//        factory.notify(Tank.Shoot(bullet))
//        verify(spriteContainer).addSprite(bullet)
//    }
}