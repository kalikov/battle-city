package com.kalikov.game

class MoveVert(private val moveable: Moveable) : MoveProperty {
    override var value: Int
        get() = moveable.y.toInt()
        set(value) {
            moveable.y = px(value)
        }
}