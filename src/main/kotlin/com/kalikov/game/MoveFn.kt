package com.kalikov.game

import java.time.Clock

class MoveFn(
    private val property: MoveProperty,
    private val endValue: Int,
    private val duration: Int,
    private val script: ScriptCallback,
    clock: Clock
) : ScriptNode {
    private val timer = BasicTimer(clock, 1, ::move)
    private val startValue = property.value

    private var isActive = true
    private var elapsed = 0L

    override val isDisposable: Boolean
        get() = false

    override fun update() {
        if (!isActive) {
            return
        }
        if (timer.isStopped) {
            timer.restart()
        }
        timer.update()
    }

    private fun move(count: Int) {
        elapsed += count
        val percent = (elapsed * 100) / duration

        val newValue = ((100 * startValue + percent * (endValue - startValue)) / 100).toInt()

        val completed = when (endValue) {
            in (startValue + 1)..newValue -> {
                property.value = endValue
                true
            }

            in newValue..<startValue -> {
                property.value = endValue
                true
            }

            else -> {
                property.value = newValue
                false
            }
        }
        if (completed) {
            isActive = false
            script.actionCompleted()
        }
    }
}