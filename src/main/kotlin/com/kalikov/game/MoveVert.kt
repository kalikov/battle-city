package com.kalikov.game

class MoveVert(private val moveable: Moveable) : MoveProperty {
    override var value: Int
        get() = moveable.y
        set(value) {
            moveable.y = value
        }
}