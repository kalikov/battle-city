package com.kalikov.game

interface ScriptNode {
    val isDisposable: Boolean

    fun update()
}