//package com.kalikov.game
//
//import com.kalikov.engine.EventManager
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.verify
//import kotlin.test.assertContentEquals
//import kotlin.test.assertFalse
//import kotlin.test.assertTrue
//
//class DefaultSpriteContainerTest {
//    @Test
//    fun `should subscribe`() {
//        val eventManager: EventManager = mock()
//
//        val spriteContainer = DefaultSpriteContainer(eventManager)
//        verify(eventManager).addSubscriber(spriteContainer, arrayOf(AbstractSprite.Destroyed::class))
//    }
//
//    @Test
//    fun `should unsubscribe`() {
//        val eventManager: EventManager = mock()
//
//        val spriteContainer = DefaultSpriteContainer(eventManager)
//        spriteContainer.dispose()
//        verify(eventManager).removeSubscriber(spriteContainer, arrayOf(AbstractSprite.Destroyed::class))
//    }
//
//    @Test
//    fun `should remove sprite when it is destroyed`() {
//        val eventManager: EventManager = mock()
//        val sprite = mockSprite()
//
//        val spriteContainer = DefaultSpriteContainer(eventManager)
//        spriteContainer.addSprite(sprite)
//
//        assertTrue(spriteContainer.containsSprite(sprite))
//        spriteContainer.notify(AbstractSprite.Destroyed(sprite))
//        assertFalse(spriteContainer.containsSprite(sprite))
//    }
//
//    @Test
//    fun `should add sprite when it is created`() {
//        val eventManager: EventManager = mock()
//        val sprite = mockSprite()
//        val spriteContainer = DefaultSpriteContainer(eventManager)
//
//        assertFalse(spriteContainer.containsSprite(sprite))
//        spriteContainer.addSprite(sprite)
//        assertTrue(spriteContainer.containsSprite(sprite))
//    }
//
//    @Test
//    fun `should sort sprites by their z-index`() {
//        val eventManager: EventManager = mock()
//
//        val spriteOne = mockSprite()
//        spriteOne.z = 1
//
//        val spriteTwo = mockSprite()
//        spriteTwo.z = 2
//
//        val spriteThree = mockSprite()
//        spriteThree.z = 3
//
//        val spriteFour = mockSprite()
//        spriteFour.z = 4
//
//        val spriteFive = mockSprite()
//        spriteFive.z = 5
//
//        val spriteContainer = DefaultSpriteContainer(eventManager)
//        spriteContainer.addSprite(spriteFour)
//        spriteContainer.addSprite(spriteOne)
//        spriteContainer.addSprite(spriteThree)
//        spriteContainer.addSprite(spriteFive)
//        spriteContainer.addSprite(spriteTwo)
//
//        val sprites = ArrayList<AbstractSprite>()
//        spriteContainer.forEach { sprites.add(it) }
//        assertContentEquals(
//            listOf(spriteOne, spriteTwo, spriteThree, spriteFour, spriteFive),
//            sprites
//        )
//    }
//
//    private fun mockSprite(): AbstractSprite {
//        return mock()
//    }
//}