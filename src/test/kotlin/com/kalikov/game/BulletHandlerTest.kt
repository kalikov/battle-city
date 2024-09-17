package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.awt.image.BufferedImage
import kotlin.test.assertEquals

class BulletHandlerTest {
    private lateinit var fonts: TestFonts

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
    }

    @Test
    fun `should subscribe`() {
        val eventManager: EventManager = mock()

        val factory = BulletHandler(eventManager, mock())
        verify(eventManager).addSubscriber(factory, setOf(Tank.Shoot::class))
    }

    @Test
    fun `should unsubscribe`() {
        val eventManager: EventManager = mock()

        val factory = BulletHandler(eventManager, mock())
        factory.dispose()
        verify(eventManager).removeSubscriber(factory, setOf(Tank.Shoot::class))
    }

    @Test
    fun `should add bullet to container when tank shoots`() {
        val spriteContainer: SpriteContainer = mock()
        val factory = BulletHandler(mock(), spriteContainer)

        val bullet = stubBullet(tank = stubEnemyTank())
        factory.notify(Tank.Shoot(bullet))
        verify(spriteContainer).addSprite(bullet)
    }
}