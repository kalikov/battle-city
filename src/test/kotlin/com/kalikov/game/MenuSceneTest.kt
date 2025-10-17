package com.kalikov.game

import com.kalikov.engine.AwtScreenSurface
import com.kalikov.engine.EventManager
import com.kalikov.engine.Keyboard
import com.kalikov.engine.Menu
import com.kalikov.engine.MenuItem
import com.kalikov.engine.Screen
import com.kalikov.engine.px
import com.kalikov.util.TestClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.awt.image.BufferedImage
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MenuSceneTest {
    private lateinit var fonts: TestFonts
    private lateinit var clock: TestClock
    private lateinit var eventManager: EventManager
    private lateinit var game: BattleCityGame
    private lateinit var stageManager: StageManager
    private lateinit var scene: MenuScene
    private lateinit var menu: Menu
    private lateinit var item1: MenuItem
    private lateinit var item2: MenuItem

    @BeforeEach
    fun beforeEach() {
        fonts = TestFonts()
        clock = TestClock()
        eventManager = mock()
        val screen: Screen = mock {
            on { createSurface(px(anyInt()), px(anyInt())) } doAnswer {
                val image = BufferedImage(it.getArgument(0), it.getArgument(1), BufferedImage.TYPE_INT_ARGB)
                AwtScreenSurface(fonts, image)
            }
        }
        game = mockGame(screen = screen, eventManager = eventManager, imageManager = TestImageManager(fonts), clock = clock)
        stageManager = game.stageManager
        item1 = mock()
        item2 = mock()
        menu = Menu(item1, item2)
        scene = MenuScene(game, mock(), menu)
    }

    @Test
    fun `should draw main menu scene`() {
        scene.arrive()
        val player = Player(game)
        whenever(stageManager.players).thenReturn(listOf(player))
        whenever(stageManager.highScore).thenReturn(20000)
        scene = MenuScene(game, mock())
        scene.arrive()

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
        scene.arrive()
        val player1 = Player(game)
        val player2 = Player(game)
        whenever(stageManager.players).thenReturn(listOf(player1, player2))
        whenever(stageManager.highScore).thenReturn(20000)
        scene = MenuScene(game, mock())
        scene.arrive()

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
        scene.activate()
        verify(eventManager).addSubscriber(scene, arrayOf(Keyboard.KeyPressed::class))
    }

    @Test
    fun `should unsubscribe on deactivate`() {
        scene.deactivate()
        verify(eventManager).removeSubscriber(scene, arrayOf(Keyboard.KeyPressed::class))
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
        scene.activate()
        scene.update()
        assertEquals(px(2), scene.top)

        clock.tick(MenuScene.MOVE_INTERVAL)
        scene.update()
        assertEquals(px(1), scene.top)
    }

    @Test
    fun `should update position when multiple intervals elapsed`() {
        scene.top = px(4)
        scene.activate()
        scene.update()
        assertEquals(px(4), scene.top)

        clock.tick(3L * MenuScene.MOVE_INTERVAL)
        scene.update()
        assertEquals(px(1), scene.top)
    }

    @Test
    fun `should activate main menu controller when arrived`() {
        scene.activate()
        assertFalse(scene.isActive)
        scene.arrive()
        assertFalse(scene.isActive)
        scene.update()
        assertTrue(scene.isActive)
    }

    @Test
    fun `should arrive`() {
        scene.activate()
        scene.arrive()

        assertEquals(px(0), scene.top)
    }


    @Test
    fun `should move to next item on SELECT key press`() {
        scene.activate()
        scene.isActive = true
        scene.notify(Keyboard.KeyPressed(Keyboard.Key.SELECT, 0))

        assertSame(item2, menu.getCurrentItem())
    }

    @Test
    fun `should execute current item on START key press`() {
        scene.activate()
        scene.isActive = true

        scene.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))

        verify(item1).execute()
    }

    @Test
    fun `should not execute current item on START key press when not active`() {
        scene.activate()
        scene.notify(Keyboard.KeyPressed(Keyboard.Key.START, 0))

        verify(item1, never()).execute()
    }
}