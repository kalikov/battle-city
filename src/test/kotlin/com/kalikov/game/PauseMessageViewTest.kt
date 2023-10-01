package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class PauseMessageViewTest {
    private lateinit var eventManager: EventManager
    private lateinit var pauseMessageView: PauseMessageView

    @BeforeEach
    fun beforeEach() {
        eventManager = mock()
        pauseMessageView = PauseMessageView(eventManager, 0, 0, mock())
    }

    @Test
    fun `should subscribe`() {
        verify(eventManager).addSubscriber(pauseMessageView, setOf(PauseManager.Start::class, PauseManager.End::class))
    }

    @Test
    fun `should dispose`() {
        pauseMessageView.dispose()
        verify(eventManager).removeSubscriber(
            pauseMessageView,
            setOf(PauseManager.Start::class, PauseManager.End::class)
        )
    }
}