package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.awt.image.BufferedImage
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MainMenuSceneTest {
    private lateinit var fonts: TestFonts
    private lateinit var clock: TestClock
    private lateinit var eventManager: EventManager
    private lateinit var scene: MainMenuScene

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
        clock = TestClock()
        eventManager = mock()
        scene = MainMenuScene(mock(), eventManager, TestImageManager(fonts), mock(), mock(), clock)
    }

    @Test
    fun `should draw main menu scene`() {
        scene.arrived()

        val image = BufferedImage(Globals.CANVAS_WIDTH, Globals.CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB)
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("main_menu.png", image)
    }

    @Test
    fun `should subscribe for key press`() {
        verify(eventManager).addSubscriber(scene, setOf(Keyboard.KeyPressed::class))
    }

    @Test
    fun `should unsubscribe on destroy`() {
        scene.destroy()
        verify(eventManager).removeSubscriber(scene, setOf(Keyboard.KeyPressed::class))
    }

    @Test
    fun `should arrive on START key press`() {
        shouldArriveOnKey(Keyboard.Key.START)
    }

    @Test
    fun `should arrive on SELECT key press`() {
        shouldArriveOnKey(Keyboard.Key.SELECT)
    }

    private fun shouldArriveOnKey(key: Keyboard.Key) {
        scene.notify(Keyboard.KeyPressed(key))
        assertEquals(0, scene.top)
    }

    @Test
    fun `should update position when interval elapsed`() {
        scene.top = 2
        scene.update()
        assertEquals(2, scene.top)

        clock.tick(MainMenuScene.INTERVAL)
        scene.update()
        assertEquals(1, scene.top)
    }

    @Test
    fun `should update position when multiple intervals elapsed`() {
        scene.top = 4
        scene.update()
        assertEquals(4, scene.top)

        clock.tick(3L * MainMenuScene.INTERVAL)
        scene.update()
        assertEquals(1, scene.top)
    }

    @Test
    fun `should activate main menu controller when arrived`() {
        assertFalse(scene.isActive)
        scene.arrived()
        assertFalse(scene.isActive)
        scene.update()
        assertTrue(scene.isActive)
    }

    @Test
    fun `should arrive on arrived call`() {
        scene.arrived()

        assertEquals(0, scene.top)
    }
}