package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstructionSceneTest {
    private lateinit var eventManager: EventManager
    private lateinit var stageManager: StageManager

    private lateinit var constructionScene: ConstructionScene

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        stageManager = mock()
        whenever(stageManager.constructionMap).thenReturn(
            StageMapConfig(emptyList(), Point(), emptyList(), emptyList()),
        )

        constructionScene = ConstructionScene(mock(), eventManager, mock(), stageManager, mock(), mock())
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(
            constructionScene,
            setOf(Keyboard.KeyPressed::class, Builder.StructureCreated::class)
        )
    }

    @Test
    fun `should add created structure`() {
        val structure = listOf(BrickWall(eventManager, mock(), 0, 0), BrickWall(eventManager, mock(), 16, 0))
        constructionScene.notify(Builder.StructureCreated(structure, constructionScene.cursor))

        for (sprite in structure) {
            assertTrue(constructionScene.spriteContainer.containsSprite(sprite))
        }
    }

    @Test
    fun `should destroy sprite under the cursor`() {
        val cursor = mockCursor(eventManager = eventManager)
        cursor.setPosition(2, 3)

        val wallOne = mockBrickWall(eventManager, x = 2, y = 3)
        val wallTwo = mockBrickWall(eventManager, x = 6, y = 3)
        val wallThree = mockBrickWall(eventManager, x = 10, y = 3)
        val wallFour = mockBrickWall(eventManager, x = 10, y = 7)

        val structureOne = listOf(wallOne, wallTwo)
        val structureTwo = listOf(wallThree, wallFour)

        constructionScene.notify(Builder.StructureCreated(structureOne, cursor))
        constructionScene.notify(Builder.StructureCreated(structureTwo, cursor))

        assertTrue(wallOne.isDestroyed)
        assertTrue(wallTwo.isDestroyed)
        assertFalse(wallThree.isDestroyed)
        assertFalse(wallFour.isDestroyed)
    }

    @Test
    fun `should add sprites of structure created`() {
        val wallOne = mockBrickWall(eventManager)
        val wallTwo = mockBrickWall(eventManager)

        val cursor = mockCursor(eventManager = eventManager)

        constructionScene.notify(Builder.StructureCreated(listOf(wallOne, wallTwo), cursor))

        assertTrue(constructionScene.spriteContainer.containsSprite(wallOne))
        assertTrue(constructionScene.spriteContainer.containsSprite(wallTwo))
    }
}