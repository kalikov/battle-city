package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.kotlin.UseConstructor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConcurrentSpriteContainerTest {
    @Test
    fun `should subscribe`() {
        val eventManager: EventManager = mock()

        val spriteContainer = ConcurrentSpriteContainer(eventManager)
        verify(eventManager).addSubscriber(spriteContainer, setOf(Sprite.Destroyed::class))
    }

    @Test
    fun `should unsubscribe`() {
        val eventManager: EventManager = mock()

        val spriteContainer = ConcurrentSpriteContainer(eventManager)
        spriteContainer.dispose()
        verify(eventManager).removeSubscriber(spriteContainer, setOf(Sprite.Destroyed::class))
    }

    @Test
    fun `should remove sprite when it is destroyed`() {
        val eventManager: EventManager = mock()
        val sprite = mockSprite(eventManager)

        val spriteContainer = ConcurrentSpriteContainer(eventManager)
        spriteContainer.addSprite(sprite)

        assertTrue(spriteContainer.containsSprite(sprite))
        spriteContainer.notify(Sprite.Destroyed(sprite))
        assertFalse(spriteContainer.containsSprite(sprite))
    }

    @Test
    fun `should add sprite when it is created`() {
        val eventManager: EventManager = mock()
        val sprite = mockSprite(eventManager)
        val spriteContainer = ConcurrentSpriteContainer(eventManager)

        assertFalse(spriteContainer.containsSprite(sprite))
        spriteContainer.addSprite(sprite)
        assertTrue(spriteContainer.containsSprite(sprite))
    }

    @Test
    fun `should sort sprites by their z-index`() {
        val eventManager: EventManager = mock()

        val spriteOne = mockSprite(eventManager)
        spriteOne.z = 1

        val spriteTwo = mockSprite(eventManager)
        spriteTwo.z = 2

        val spriteThree = mockSprite(eventManager)
        spriteThree.z = 3

        val spriteFour = mockSprite(eventManager)
        spriteFour.z = 4

        val spriteFive = mockSprite(eventManager)
        spriteFive.z = 5

        val spriteContainer = ConcurrentSpriteContainer(eventManager)
        spriteContainer.addSprite(spriteFour)
        spriteContainer.addSprite(spriteOne)
        spriteContainer.addSprite(spriteThree)
        spriteContainer.addSprite(spriteFive)
        spriteContainer.addSprite(spriteTwo)

        assertContentEquals(
            listOf(spriteOne, spriteTwo, spriteThree, spriteFour, spriteFive),
            spriteContainer.sprites
        )
    }

    private fun mockSprite(eventManager: EventManager): Sprite {
        return mock(useConstructor = UseConstructor.withArguments(eventManager, 0, 0, 1, 1))
    }
}