package com.kalikov.game

class MoveHorz(private val moveable: Moveable) : MoveProperty {
    override var value: Int
        get() = moveable.x
        set(value) {
            moveable.x = value
        }
}