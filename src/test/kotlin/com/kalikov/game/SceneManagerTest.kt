package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
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

        sceneManager.setScene { scene }
        sceneManager.update()
        verify(scene).update()
    }

    @Test
    fun `should draw scene on draw`() {
        val scene: Scene = mock()
        val screenSurface: ScreenSurface = mock()

        sceneManager.setScene { scene }
        sceneManager.draw(screenSurface)
        verify(scene).draw(screenSurface)
    }

    @Test
    fun `should have no scene by default`() {
        assertNull(sceneManager.scene)
    }

    @Test
    fun `should destroy previous scene`() {
        val scene1: Scene = mock()
        val scene2: Scene = mock()

        sceneManager.setScene { scene1 }
        sceneManager.setScene { scene2 }

        verify(scene1).destroy()
    }
}