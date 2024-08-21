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
        val builder = BaseWallBuilder(eventManager, spriteContainer, Rect(), Point())

        verify(eventManager).addSubscriber(
            builder,
            setOf(SpriteContainer.Added::class, SpriteContainer.Removed::class)
        )
    }

    @Test
    fun `should unsubscribe`() {
        eventManager = mock()
        val builder = BaseWallBuilder(eventManager, spriteContainer, Rect(), Point())
        builder.dispose()

        verify(eventManager).removeSubscriber(
            builder,
            setOf(SpriteContainer.Added::class, SpriteContainer.Removed::class)
        )
    }

    @Test
    fun `should build base wall`() {
        val builder = BaseWallBuilder(
            eventManager,
            spriteContainer,
            Rect(0, 0, 13 * Globals.UNIT_SIZE, 13 * Globals.UNIT_SIZE),
            Point(6 * Globals.UNIT_SIZE, 12 * Globals.UNIT_SIZE)
        )
        builder.buildWall(BrickWallFactory(eventManager, mock()))

        assertEquals(8, spriteContainer.size)

        val positions = HashSet<Point>()
        spriteContainer.forEach {
            positions.add(Point(it.x, it.y))
        }
        assertEquals(
            setOf(
                Point(11 * Globals.TILE_SIZE, 25 * Globals.TILE_SIZE),
                Point(11 * Globals.TILE_SIZE, 24 * Globals.TILE_SIZE),
                Point(11 * Globals.TILE_SIZE, 23 * Globals.TILE_SIZE),
                Point(12 * Globals.TILE_SIZE, 23 * Globals.TILE_SIZE),
                Point(13 * Globals.TILE_SIZE, 23 * Globals.TILE_SIZE),
                Point(14 * Globals.TILE_SIZE, 23 * Globals.TILE_SIZE),
                Point(14 * Globals.TILE_SIZE, 24 * Globals.TILE_SIZE),
                Point(14 * Globals.TILE_SIZE, 25 * Globals.TILE_SIZE),
            ),
            positions
        )
    }

    @Test
    fun `should destroy base wall`() {
        val builder = BaseWallBuilder(
            eventManager,
            spriteContainer,
            Rect(0, 0, 13 * Globals.UNIT_SIZE, 13 * Globals.UNIT_SIZE),
            Point(6 * Globals.UNIT_SIZE, 12 * Globals.UNIT_SIZE)
        )

        val brickWall = mockBrickWall(eventManager, x = 11 * Globals.TILE_SIZE, y = 25 * Globals.TILE_SIZE)
        spriteContainer.addSprite(brickWall)

        builder.destroyWall()

        assertTrue(brickWall.isDestroyed)
    }

    @Test
    fun `should not destroy non-base wall`() {
        val builder = BaseWallBuilder(
            eventManager,
            spriteContainer,
            Rect(0, 0, 13 * Globals.UNIT_SIZE, 13 * Globals.UNIT_SIZE),
            Point()
        )

        val brickWall = mockBrickWall(eventManager)
        spriteContainer.addSprite(brickWall)

        builder.destroyWall()

        assertFalse(brickWall.isDestroyed)
    }
}