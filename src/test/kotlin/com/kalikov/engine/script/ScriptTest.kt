package com.kalikov.engine.script

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScriptTest {
    open class MockAction(private val script: ScriptCallback, private var numUpdates: Int) : ScriptNode {
        override val isDisposable get() = false

        override fun update() {
            numUpdates--
            if (numUpdates == 0) {
                script.actionCompleted()
            }
        }
    }

    @Test
    fun `default script is done`() {
        val script = Script()
        assertTrue(script.isDone)
        assertEquals(0, script.size)
    }

    @Test
    fun `script is not done after enqueue`() {
        val script = Script()
        script.enqueue(Execute {})
        assertFalse(script.isDone)
        assertEquals(1, script.size)

        script.update()
        assertTrue(script.isDone)
        assertEquals(0, script.size)
    }

    @Test
    fun `should run and complete script`() {
        val script = Script()

        val commandOne: ScriptNode = mock()
        whenever(commandOne.isDisposable).thenReturn(true)
        val commandTwo: ScriptNode = mock()
        whenever(commandTwo.isDisposable).thenReturn(true)
        val actionOne = spy(MockAction(script, 2))
        val actionTwo = spy(MockAction(script, 1))

        script.enqueue(commandOne)
        script.enqueue(commandTwo)
        script.enqueue(actionOne)
        script.enqueue(actionTwo)

        script.update()

        verify(commandOne).update()
        verify(commandTwo).update()
        verify(actionOne).update()
        verify(actionTwo, never()).update()

        script.update()

        verify(commandOne).update()
        verify(commandTwo).update()
        verify(actionOne, times(2)).update()
        verify(actionTwo, never()).update()

        script.update()

        verify(commandOne).update()
        verify(commandTwo).update()
        verify(actionOne, times(2)).update()
        verify(actionTwo).update()

        script.update()
        assertTrue(script.isDone)
    }
}