package com.kalikov.game

import com.kalikov.engine.BasicSceneManager
import com.kalikov.engine.EventManager
import com.kalikov.engine.Scene
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import kotlin.test.assertNull

class SceneManagerTest {
    private lateinit var eventManager: EventManager
    private lateinit var sceneManager: BasicSceneManager

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        sceneManager = BasicSceneManager()
    }

    @Test
    fun `should update scene on update`() {
        val scene: Scene = mock()

        sceneManager.setNextScene(scene)
        sceneManager.update()
        verify(scene).update()
    }

    @Test
    fun `should not update scene on draw`() {
        val scene: Scene = mock()

        sceneManager.setNextScene(scene)

        assertNull(sceneManager.scene)
    }

    @Test
    fun `should have no scene by default`() {
        assertNull(sceneManager.scene)
    }

    @Test
    fun `should not deactivate not activated scene`() {
        val scene1: Scene = mock()
        val scene2: Scene = mock()

        sceneManager.setNextScene(scene1)
        sceneManager.setNextScene(scene2)
        sceneManager.update()

        verify(scene1, never()).activate()
        verify(scene1, never()).deactivate()
        verify(scene2).activate()
    }

    @Test
    fun `should deactivate previous scene`() {
        val scene1: Scene = mock()
        val scene2: Scene = mock()

        sceneManager.setNextScene(scene1)
        sceneManager.update()

        verify(scene1).activate()

        sceneManager.setNextScene(scene2)
        sceneManager.update()

        verify(scene1).deactivate()
        verify(scene2).activate()
    }
}