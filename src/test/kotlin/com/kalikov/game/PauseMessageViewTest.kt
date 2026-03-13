package com.kalikov.game

import com.kalikov.engine.ARGB
import com.kalikov.engine.ScreenSurface
import com.kalikov.engine.px
import com.kalikov.util.TestClock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

class PauseMessageViewTest {
    private lateinit var clock: TestClock
    private lateinit var pauseManager: PauseManager
    private lateinit var pauseMessageView: PauseMessageView

    @BeforeEach
    fun beforeEach() {
        clock = TestClock()
        pauseManager = mock()
        pauseMessageView = PauseMessageView(pauseManager, 0.px, 0.px, clock)
    }

    @Test
    fun `should not draw view when timer is stopped`() {
        val surface: ScreenSurface = mock()
        pauseMessageView.update()
        pauseMessageView.draw(surface)
        verifyNoInteractions(surface)
    }

    @Test
    fun `should draw view when timer is running`() {
        whenever(pauseManager.isPaused).doReturn(true)
        val surface: ScreenSurface = mock()
        pauseMessageView.update()
        pauseMessageView.draw(surface)
        verify(surface).fillText(anyString(), anyInt().px, anyInt().px, ARGB(anyInt()), anyString(), anyOrNull())
    }

    @Test
    fun `should not draw view when timer is running in transparent state`() {
        whenever(pauseManager.isPaused).doReturn(true)
        val surface: ScreenSurface = mock()
        pauseMessageView.update()

        clock.tick(PauseMessageView.INTERVAL)
        pauseMessageView.update()
        pauseMessageView.draw(surface)
        verifyNoInteractions(surface)

        clock.tick(PauseMessageView.INTERVAL - 1)
        pauseMessageView.update()
        pauseMessageView.draw(surface)
        verifyNoInteractions(surface)

        clock.tick(1)
        pauseMessageView.update()
        pauseMessageView.draw(surface)
        verify(surface).fillText(anyString(), anyInt().px, anyInt().px, ARGB(anyInt()), anyString(), anyOrNull())
    }
}