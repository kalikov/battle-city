package com.kalikov.engine.script

import com.kalikov.engine.px

class MoveVert(private val moveable: Moveable) : MoveProperty {
    override var value: Int
        get() = moveable.y.toInt()
        set(value) {
            moveable.y = value.px
        }
}