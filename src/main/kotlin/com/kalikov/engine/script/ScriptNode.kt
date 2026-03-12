package com.kalikov.engine.script

interface ScriptNode {
    val isDisposable: Boolean

    fun update()
}