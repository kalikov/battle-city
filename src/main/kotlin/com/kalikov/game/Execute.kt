package com.kalikov.game

class Execute(private val action: () -> Unit) : ScriptNode {
    override val isDisposable get() = true

    override fun update() {
        action()
    }
}