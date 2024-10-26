package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import kotlin.test.assertNull

class SceneManagerTest {
    private lateinit var eventManager: EventManager
    private lateinit var sceneManager: SceneManager

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        sceneManager = SceneManager(eventManager)
    }

    @Test
    fun `should update scene on update`() {
        val scene: Scene = mock()

        sceneManager.setNextScene { scene }
        sceneManager.update()
        verify(scene).update()
    }

    @Test
    fun `should not update scene on draw`() {
        val scene: Scene = mock()
        val screenSurface: ScreenSurface = mock()

        sceneManager.setNextScene { scene }
        sceneManager.draw(screenSurface)
        verify(scene, never()).draw(screenSurface)
    }

    @Test
    fun `should have no scene by default`() {
        assertNull(sceneManager.scene)
    }

    @Test
    fun `should destroy previous scene`() {
        val scene1: Scene = mock()
        val scene2: Scene = mock()

        sceneManager.setNextScene { scene1 }
        sceneManager.setNextScene { scene2 }

        verify(scene1).destroy()
    }
}