package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MainMenuSceneTest {
    private lateinit var fonts: TestFonts
    private lateinit var clock: TestClock
    private lateinit var eventManager: EventManager
    private lateinit var game: Game
    private lateinit var stageManager: StageManager
    private lateinit var scene: MainMenuScene

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
        clock = TestClock()
        eventManager = mock()
        stageManager = mock()
        game = mockGame(eventManager = eventManager, imageManager = TestImageManager(fonts), clock = clock)
        scene = MainMenuScene(game, stageManager)
    }

    @Test
    fun `should draw main menu scene`() {
        scene.arrived()
        val player = Player(game)
        whenever(stageManager.players).thenReturn(listOf(player))
        whenever(stageManager.highScore).thenReturn(20000)

        val image = BufferedImage(
            Globals.CANVAS_WIDTH.toInt(),
            Globals.CANVAS_HEIGHT.toInt(),
            BufferedImage.TYPE_INT_ARGB
        )
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("main_menu.png", image)
    }

    @Test
    fun `should draw main menu scene with two players`() {
        scene.arrived()
        val player1 = Player(game)
        val player2 = Player(game)
        whenever(stageManager.players).thenReturn(listOf(player1, player2))
        whenever(stageManager.highScore).thenReturn(20000)

        val image = BufferedImage(
            Globals.CANVAS_WIDTH.toInt(),
            Globals.CANVAS_HEIGHT.toInt(),
            BufferedImage.TYPE_INT_ARGB
        )
        scene.draw(AwtScreenSurface(fonts, image))

        assertImageEquals("main_menu_two_players.png", image)
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
        scene.notify(Keyboard.KeyPressed(key, 0))
        assertEquals(px(0), scene.top)
    }

    @Test
    fun `should update position when interval elapsed`() {
        scene.top = px(2)
        scene.update()
        assertEquals(px(2), scene.top)

        clock.tick(MainMenuScene.MOVE_INTERVAL)
        scene.update()
        assertEquals(px(1), scene.top)
    }

    @Test
    fun `should update position when multiple intervals elapsed`() {
        scene.top = px(4)
        scene.update()
        assertEquals(px(4), scene.top)

        clock.tick(3L * MainMenuScene.MOVE_INTERVAL)
        scene.update()
        assertEquals(px(1), scene.top)
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

        assertEquals(px(0), scene.top)
    }
}