package com.kalikov.game

import com.kalikov.engine.script.ScriptNode

class StageSelect() : ScriptNode {
    override val isDisposable get() = false

    var isActive = false

    override fun update() {
        isActive = true
    }
}