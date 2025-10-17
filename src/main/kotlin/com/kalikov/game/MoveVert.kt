package com.kalikov.game

import com.kalikov.engine.px

class MoveVert(private val moveable: Moveable) : MoveProperty {
    override var value: Int
        get() = moveable.y.toInt()
        set(value) {
            moveable.y = px(value)
        }
}