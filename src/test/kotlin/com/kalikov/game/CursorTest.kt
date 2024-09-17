package com.kalikov.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class CursorTest {
    private lateinit var builder: BuilderHandler
    private lateinit var cursor: Cursor

    @BeforeEach
    fun beforeEach() {
        builder = mock()
        cursor = Cursor(mockGame(), builder)
    }

    @Test
    fun `should build`() {
        cursor.build()

        verify(builder).build(cursor)
    }

    @Test
    fun `should build next`() {
        cursor.buildNext()

        val order = inOrder(builder)
        order.verify(builder).nextStructure()
        order.verify(builder).build(cursor)
    }
}