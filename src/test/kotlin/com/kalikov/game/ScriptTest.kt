package com.kalikov.game

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertTrue

class ScriptTest {
    open class MockAction(private val script: ScriptCallback, private var numUpdates: Int) : ScriptNode {
        override val isDisposable: Boolean get() = false

        override fun update() {
            numUpdates--
            if (numUpdates == 0) {
                script.actionCompleted()
            }
        }
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
        assertTrue(script.isEmpty)
    }
}