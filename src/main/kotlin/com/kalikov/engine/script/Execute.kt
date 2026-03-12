package com.kalikov.engine.script

class Execute(private val action: () -> Unit) : ScriptNode {
    override val isDisposable get() = true

    override fun update() {
        action()
    }
}