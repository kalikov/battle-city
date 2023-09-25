package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BaseWallBuilderTest {
    private lateinit var eventManager: EventManager
    private lateinit var spriteContainer: SpriteContainer

    @BeforeEach
    fun beforeEach() {
        eventManager = ConcurrentEventManager()
        spriteContainer = ConcurrentSpriteContainer(eventManager)
    }

    @Test
    fun `should subscribe`() {
        eventManager = mock()
        val builder = BaseWallBuilder(eventManager, spriteContainer, emptySet())

        verify(eventManager).addSubscriber(
            builder,
            setOf(SpriteContainer.Added::class, SpriteContainer.Removed::class)
        )
    }

    @Test
    fun `should unsubscribe`() {
        eventManager = mock()
        val builder = BaseWallBuilder(eventManager, spriteContainer, emptySet())
        builder.dispose()

        verify(eventManager).removeSubscriber(
            builder,
            setOf(SpriteContainer.Added::class, SpriteContainer.Removed::class)
        )
    }

    @Test
    fun `should build base wall`() {
        val builder = BaseWallBuilder(eventManager, spriteContainer, setOf(Point(1, 2), Point(10, 20)))
        builder.buildWall(BrickWallFactory(eventManager, mock()))

        val sprites = spriteContainer.sprites
        assertEquals(2, sprites.size)

        val positions = sprites.map { Point(it.x, it.y) }.toSet()
        assertEquals(setOf(Point(1, 2), Point(10, 20)), positions)
    }

    @Test
    fun `should destroy base wall`() {
        val builder = BaseWallBuilder(eventManager, spriteContainer, setOf(Point(0, 0)))

        val brickWall = mockBrickWall(eventManager)
        spriteContainer.addSprite(brickWall)

        builder.destroyWall()

        assertTrue(brickWall.isDestroyed)
    }

    @Test
    fun `should not destroy non-base wall`() {
        val builder = BaseWallBuilder(eventManager, spriteContainer, setOf(Point(10, 0)))

        val brickWall = mockBrickWall(eventManager)
        spriteContainer.addSprite(brickWall)

        builder.destroyWall()

        assertFalse(brickWall.isDestroyed)
    }
}