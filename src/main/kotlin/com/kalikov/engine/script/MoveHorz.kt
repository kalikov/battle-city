package com.kalikov.engine.script

import com.kalikov.engine.px

class MoveHorz(private val moveable: Moveable) : MoveProperty {
    override var value: Int
        get() = moveable.x.toInt()
        set(value) {
            moveable.x = value.px
        }
}