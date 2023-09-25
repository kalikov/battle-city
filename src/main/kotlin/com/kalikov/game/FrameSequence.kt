package com.kalikov.game

class FrameSequence(
    private val frames: IntArray,
    private val loop: Boolean = false
) {
    var isCompleted = false
        private set

    val frame get() = frames[index]

    val size get() = frames.size

    private var index = 0

    fun restart() {
        index = 0
        isCompleted = false
    }

    fun advance(count: Int) {
        index += count
        if (index >= frames.size) {
            if (loop) {
                index %= frames.size
            } else {
                index = frames.size - 1
                isCompleted = true
            }
        }
    }
}

fun frameSequenceOf(vararg frames: Int) = FrameSequence(frames)

fun frameLoopOf(vararg frames: Int) = FrameSequence(frames, true)